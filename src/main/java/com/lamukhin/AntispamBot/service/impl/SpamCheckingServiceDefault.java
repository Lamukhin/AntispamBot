package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.service.interfaces.MetadataService;
import com.lamukhin.AntispamBot.service.interfaces.SpamCheckingService;
import com.lamukhin.AntispamBot.service.interfaces.TextService;
import com.lamukhin.AntispamBot.util.MessageOperations;
import com.lamukhin.AntispamBot.verification.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;

import static com.lamukhin.AntispamBot.util.ResponseMessage.MAYBE_SPAM;
import static com.lamukhin.AntispamBot.util.ResponseMessage.SPAM_FOUND;

@Service
public class SpamCheckingServiceDefault implements SpamCheckingService {

    private final Logger log = LoggerFactory.getLogger(SpamCheckingServiceDefault.class);
    private final TextService textService;
    private final MetadataService metadataService;

    @Value("${coefficients.for_4_to_6_length}")
    private double for4To6Length;
    @Value("${coefficients.for_7_to_20_length}")
    private double for7To20Length;
    @Value("${coefficients.for_more_21_length}")
    private double forMoreThan21Length;
    private final ExecutorService executorService =
            Executors.newFixedThreadPool(10, r -> {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            });

    public SpamCheckingServiceDefault(TextService textService, MetadataService metadataService) {
        this.textService = textService;
        this.metadataService = metadataService;
    }


    @Override
    public void checkUpdate(Update update, TelegramLongPollingEngine engine) {
        if ((update.hasMessage()) && (update.getMessage().hasText())) {
            int totalMessageScore = 0;
            String[] wordsOfMessage = textService.invokeWordsFromRawMessage(update.getMessage().getText());

            List<Callable<Integer>> tasks = new ArrayList<>();
            for (String word : wordsOfMessage) {
                Search search = new Search(word, textService.getCachedDictionary());
                tasks.add(search);
            }

            try {
                List<Future<Integer>> futures = executorService.invokeAll(tasks);
                for (Future<Integer> currentFuture : futures) {
                    totalMessageScore += currentFuture.get();
                }
            } catch (InterruptedException | RuntimeException | ExecutionException ex) {
                log.error("The message checking has failed: {}", ex.getMessage());
            }

            double coefOfAllMessage = (double) totalMessageScore / wordsOfMessage.length;

            if (isSpam(coefOfAllMessage, wordsOfMessage.length)) {

                //TODO: необходимо доработать Вовину либу, потому что без этого работа с колбэками - пытка.
                String spamFoundResponse = String.format(
                        SPAM_FOUND,
                        (int) (coefOfAllMessage * 100)
                );

                MessageOperations.replyToMessage(
                        update.getMessage().getChatId(),
                        spamFoundResponse,
                        update.getMessage().getMessageId(),
                        engine);

                MessageOperations.deleteMessage(
                        update.getMessage().getChatId(),
                        update.getMessage().getMessageId(),
                        engine);
                /*
                тут архитектурный проёб, фиксить который я не собираюсь:
                хоть и ооочень маловероятно, но deleteMessage может отъебнуть,
                а значит апдейтить ничего не потребуется.
                */
                metadataService.updateDeletedMessages(engine.getBotUsername());
                textService.saveMessageIntoDictionary(wordsOfMessage);

            } else if(coefOfAllMessage >= 0.4) {

                MessageOperations.replyToMessage(
                        update.getMessage().getChatId(),
                        MAYBE_SPAM,
                        update.getMessage().getMessageId(),
                        engine);
            }
        }
    }

    // yes, im bad at math
    private boolean isSpam(double coefOfAllMessage, int amountOfWords) {
        if ((amountOfWords >= 4) && (amountOfWords <= 6)) {
            return coefOfAllMessage >= for4To6Length;
        }
        if ((amountOfWords >= 7) && (amountOfWords <= 20)) {
            return coefOfAllMessage >= for7To20Length;
        }
        if (amountOfWords >= 21) {
            return coefOfAllMessage >= forMoreThan21Length;
        }
        return false;
    }

    @PreDestroy
    private void destroyExecutorService() {
        executorService.shutdown();
    }

}
