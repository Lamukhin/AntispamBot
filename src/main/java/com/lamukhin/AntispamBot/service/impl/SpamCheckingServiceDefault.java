package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.service.interfaces.SpamCheckingService;
import okhttp3.Call;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;
import com.lamukhin.AntispamBot.verification.Search;

@Service
public class SpamCheckingServiceDefault implements SpamCheckingService {

    private final Logger log = LoggerFactory.getLogger(SpamCheckingServiceDefault.class);
    private final Map<Character, Integer> symbolsDictionary = new HashMap<>();
    private final Map<String, Integer> wordDictionary = new HashMap<>();

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


    @Override
    public void checkUpdate(Update update, TelegramLongPollingEngine engine) {
        wordDictionary.put("sex", 1);
        symbolsDictionary.put('@', 1);
        symbolsDictionary.put('+', 1);
        symbolsDictionary.put('$', 1);
        if ((update.hasMessage()) && (update.getMessage().hasText())) {
            int totalMessageScore = 0;
            String[] wordsOfMessage = invokeWordsFromRawMessage(update.getMessage().getText());

            List<Callable<Integer>> tasks = new ArrayList<>();
            for (String word : wordsOfMessage) {
                Search search = new Search(word, wordDictionary, symbolsDictionary);
                tasks.add(search);
            }

            try {
                List<Future<Integer>> futures = executorService.invokeAll(tasks);
                for (Future<Integer> currentFuture : futures){
                    totalMessageScore += currentFuture.get();
                }
            } catch (InterruptedException | RuntimeException | ExecutionException ex) {
                log.error("The message checking has failed: {}", ex.getMessage());
            }

            double coefOfAllMessage = (double) totalMessageScore / wordsOfMessage.length;
            if (isSpam(coefOfAllMessage, wordsOfMessage.length)){
                System.out.println("IT IS SPAM");
                var sendReply = SendMessage.builder()
                        .chatId(update.getMessage().getChatId())
                        .replyToMessageId(update.getMessage().getMessageId())
                        .text("Это спам!")
                        .build();
                engine.executeNotException(sendReply);

                var sendDelete = DeleteMessage.builder()
                        .chatId(update.getMessage().getChatId())
                        .messageId(update.getMessage().getMessageId())
                        .build();
                engine.executeNotException(sendDelete);
            }
            System.out.println("IT IS NOT SPAM");
        }
    }

    private String[] invokeWordsFromRawMessage(String incomeMessage) {
        incomeMessage = incomeMessage
                .toLowerCase()
                .replaceAll("[^a-zA-Zа-яА-Я0-9\s]", " ")
                .trim();
        return incomeMessage.split(" ");
    }

    // yes, im bad at math
    private boolean isSpam(double coefOfAllMessage, int amountOfWords) {
        if ((amountOfWords >= 4)&&(amountOfWords <= 6)){
            return coefOfAllMessage >= for4To6Length;
        }
        if ((amountOfWords >= 7)&&(amountOfWords <= 20)){
            return coefOfAllMessage >= for7To20Length;
        }
        if (amountOfWords >= 21){
            return coefOfAllMessage >= forMoreThan21Length;
        }
        return false;
    }


    @PreDestroy
    private void destroyExecutorService() {
        executorService.shutdown();
    }
}
