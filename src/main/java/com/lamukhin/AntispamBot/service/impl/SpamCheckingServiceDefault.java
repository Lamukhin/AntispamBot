package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.service.interfaces.SpamCheckingService;
import com.lamukhin.AntispamBot.service.interfaces.TextService;
import com.lamukhin.AntispamBot.util.MessageOperations;
import com.lamukhin.AntispamBot.verification.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;

@Service
public class SpamCheckingServiceDefault implements SpamCheckingService {

    private final Logger log = LoggerFactory.getLogger(SpamCheckingServiceDefault.class);
    private final TextService textService;

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

    public SpamCheckingServiceDefault(TextService textService) {
        this.textService = textService;
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

                //TODO: необходимо доработать Вовину либо, потому что без этого работа с колбэками - пытка.
                String response = "Подозреваю, это спам. \"Вхождение\" сообщения в словарь банвордов более "
                        + (int) (coefOfAllMessage * 100) + " %.";

                MessageOperations.replyToMessage(
                        update.getMessage().getChatId(),
                        response,
                        update.getMessage().getMessageId(),
                        engine);

                System.out.println("IT IS SPAM");

                var sendDelete = DeleteMessage.builder()
                        .chatId(update.getMessage().getChatId())
                        .messageId(update.getMessage().getMessageId())
                        .build();
                engine.executeNotException(sendDelete);
                textService.saveMessageIntoDictionary(wordsOfMessage);
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
