package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.algorithm.Search;
import com.lamukhin.AntispamBot.algorithm.SearchSettings;
import com.lamukhin.AntispamBot.db.entity.DictionaryEntity;
import com.lamukhin.AntispamBot.service.interfaces.MetadataService;
import com.lamukhin.AntispamBot.service.interfaces.SpamCheckingService;
import com.lamukhin.AntispamBot.service.interfaces.TextService;
import com.lamukhin.AntispamBot.util.Commands;
import com.lamukhin.AntispamBot.util.MessageOperations;
import com.lamukhin.AntispamBot.util.TextFiltrationProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatMember;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import jakarta.annotation.PreDestroy;
import ru.wdeath.telegram.bot.starter.TelegramLongPollingEngine;

import java.util.ArrayList;
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
        if ((update.hasMessage()) && (update.getMessage().hasText())) {
            int totalMessageScore = 0;
            boolean thisIsTest = false;
            String incomeMessage = update.getMessage().getText();
            if (thisIsCommand(incomeMessage)) {
                log.warn("This message is command. Spam checking stopped.");
                return;
            }
            log.warn("This message is NOT command. Spam checking continues.");
            if (incomeMessage.startsWith("!тест")) {
                thisIsTest = true;
                incomeMessage = incomeMessage.replaceFirst("!тест", "");
            }
            String[] wordsOfMessage = textService.invokeWordsFromRawMessage(
                    incomeMessage,
                    TextFiltrationProps.NO_SHORTS);

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

            double coefOfAllMessage = (double) totalMessageScore / (double) wordsOfMessage.length;
            log.warn("Result of search: found {} of {} words, final coef of all message {}", totalMessageScore, wordsOfMessage.length, coefOfAllMessage);

            String testPostfix = thisIsTest ? "Это был тестовый запрос." : "";
            if ((isSpam(coefOfAllMessage, wordsOfMessage.length) == 1)
                    || (isSpam(coefOfAllMessage, wordsOfMessage.length) == 0)) {

                String spamFoundResponse = String.format(
                        SPAM_FOUND,
                        (int) (coefOfAllMessage * 100),
                        testPostfix
                );

                MessageOperations.replyToMessage(
                        update.getMessage().getChatId(),
                        spamFoundResponse,
                        update.getMessage().getMessageId(),
                        engine);

                if (!thisIsTest) {
                    var send = BanChatMember.builder()
                            .chatId(update.getMessage().getChatId())
                            .userId(update.getMessage().getFrom().getId())
                            .revokeMessages(true)
                            .untilDate(0)
                            .build();
                    engine.executeNotException(send);

                    MessageOperations.deleteMessage(
                            update.getMessage().getChatId(),
                            update.getMessage().getMessageId(),
                            engine);

                    metadataService.updateDeletedMessages(engine.getBotUsername());
                    metadataService.updateBannedUsers(engine.getBotUsername());
                    textService.saveMessageIntoDictionary(wordsOfMessage);
                } else {
                    log.warn("IT WAS TEST REQUEST, DATA IS NOT SAVED!");
                }

            } else if (Double.compare(coefOfAllMessage, searchSettings.getCoefForLowerLimit()) == 1) {
                MessageOperations.replyToMessage(
                        update.getMessage().getChatId(),
                        String.format(MAYBE_SPAM, testPostfix),
                        update.getMessage().getMessageId(),
                        engine);
            }
        }
    }

    private boolean thisIsCommand(String incomeMessage) {
        List<BotCommand> allCommands = new ArrayList<>() {
            {
                addAll(Commands.getDefaultUserCommands());
                addAll(Commands.getAdminCommands());
                addAll(Commands.getOwnerCommands());
            }
        };
        return allCommands.stream()
                .anyMatch(current -> incomeMessage.contains(current.getCommand()));
    }

    private List<Callable<Integer>> createSearchingTasks(String[] wordsOfMessage, Map<String, DictionaryEntity> cachedDictionary, SearchSettings searchSettings) {
        List<Callable<Integer>> tasks = new ArrayList<>();
        for (String word : wordsOfMessage) {
            Search search = new Search(word, textService.getCachedDictionary(), searchSettings);
            tasks.add(search);
        }
        return tasks;
    }

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


