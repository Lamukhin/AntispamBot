package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.algorithm.Search;
import com.lamukhin.AntispamBot.algorithm.SearchSettings;
import com.lamukhin.AntispamBot.db.entity.DictionaryEntity;
import com.lamukhin.AntispamBot.service.interfaces.MetadataService;
import com.lamukhin.AntispamBot.service.interfaces.SpamCheckingService;
import com.lamukhin.AntispamBot.service.interfaces.TextService;
import com.lamukhin.AntispamBot.util.MessageOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatMember;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.lamukhin.AntispamBot.util.ResponseMessage.MAYBE_SPAM;
import static com.lamukhin.AntispamBot.util.ResponseMessage.SPAM_FOUND;

@Service
public class SpamCheckingServiceDefault implements SpamCheckingService {

    private final TextService textService;
    private final MetadataService metadataService;
    private final SearchSettings searchSettings;
    private final ExecutorService executorService =
            Executors.newFixedThreadPool(10, r -> {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            });

    private final Logger log = LoggerFactory.getLogger(SpamCheckingServiceDefault.class);


    @Override
    public void checkUpdate(Update update, TelegramLongPollingEngine engine) {
        //TODO: не уверен, что тут не будет npe
        if ((update.hasMessage()) && (update.getMessage().hasText())) {
            int totalMessageScore = 0;
            String[] wordsOfMessage = textService.invokeWordsFromRawMessage(update.getMessage().getText());
            //я хочу на будущее и предлоги/частицы себе сохранить, но в данном месте это ломает логику.
            //поэтому для выведения кэфа я профильтрую без них, а сохраню в базу с ними
            //TODO: исправить этот кринж (когда-нибудь)
            String[] wordsOfMessageBEZ_PREDLOGOV = deleteShortWords(wordsOfMessage);

            List<Callable<Integer>> tasks = createSearchingTasks(
                    wordsOfMessage,
                    textService.getCachedDictionary(),
                    searchSettings);

            try {
                List<Future<Integer>> futures = executorService.invokeAll(tasks);
                for (Future<Integer> currentFuture : futures) {
                    totalMessageScore += currentFuture.get();
                }
            } catch (InterruptedException | RuntimeException | ExecutionException ex) {
                log.error("The message checking has failed: {}", ex.getMessage());
            }
            log.warn("Result of search: found {} of {}",totalMessageScore, wordsOfMessageBEZ_PREDLOGOV.length);
            double coefOfAllMessage = (double) totalMessageScore / wordsOfMessageBEZ_PREDLOGOV.length;

            //TODO: зарефакторить это уродство с int и bool
            if ((isSpam(coefOfAllMessage, wordsOfMessageBEZ_PREDLOGOV.length) == 1)
                    ||(isSpam(coefOfAllMessage, wordsOfMessageBEZ_PREDLOGOV.length) == 0)) {
                String spamFoundResponse = String.format(
                        SPAM_FOUND,
                        (int) (coefOfAllMessage * 100)
                );

                MessageOperations.replyToMessage(
                        update.getMessage().getChatId(),
                        spamFoundResponse,
                        update.getMessage().getMessageId(),
                        engine);

                var send = BanChatMember.builder()
                        .chatId(update.getMessage().getChatId())
                        .userId(update.getMessage().getFrom().getId())
                        .revokeMessages(true)
                        .untilDate(0)
                        .build();
                engine.executeNotException(send);


//                MessageOperations.deleteMessage(
//                        update.getMessage().getChatId(),
//                        update.getMessage().getMessageId(),
//                        engine);

                metadataService.updateDeletedMessages(engine.getBotUsername());
                metadataService.updateBannedUsers(engine.getBotUsername());
                textService.saveMessageIntoDictionary(wordsOfMessage);

            } else if (Double.compare(coefOfAllMessage, searchSettings.getCoefForLowerLimit()) == 1) {
                MessageOperations.replyToMessage(
                        update.getMessage().getChatId(),
                        MAYBE_SPAM,
                        update.getMessage().getMessageId(),
                        engine);
            }
        }
    }

    private String[] deleteShortWords(String[] wordsOfMessage) {
        return Arrays.stream(wordsOfMessage)
                .filter(word -> (word.length() >= 3))
                .toArray(String[]::new);
    }

    private List<Callable<Integer>> createSearchingTasks(String[] wordsOfMessage, Map<String, DictionaryEntity> cachedDictionary, SearchSettings searchSettings) {
        List<Callable<Integer>> tasks = new ArrayList<>();
        for (String word : wordsOfMessage) {
            Search search = new Search(word, textService.getCachedDictionary(), searchSettings);
            tasks.add(search);
        }
        return tasks;
    }

    // yes, im bad at math
    private int isSpam(double coefOfAllMessage, int amountOfWords) {
        if (searchSettings.getSegmentForShort().isInSegment(amountOfWords)) {
            return Double.compare(coefOfAllMessage, searchSettings.getCoefForShortMessage());
        }
        if (searchSettings.getSegmentForMiddle().isInSegment(amountOfWords)) {
            return Double.compare(coefOfAllMessage, searchSettings.getCoefForMiddleMessage());
        }
        if (searchSettings.getSegmentForLong().isInSegment(amountOfWords)) {
            return Double.compare(coefOfAllMessage, searchSettings.getCoefForLongMessage());
        }
        return -1;
    }

    @PreDestroy
    private void destroyExecutorService() {
        executorService.shutdown();
    }

    public SpamCheckingServiceDefault(TextService textService, MetadataService metadataService, SearchSettings searchSettings) {
        this.textService = textService;
        this.metadataService = metadataService;
        this.searchSettings = searchSettings;
    }
}
