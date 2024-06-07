package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.service.interfaces.SpamCheckingService;
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
    private Map<String, Integer> wordDictionary = new HashMap<>();

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
        if ((update.hasMessage()) && (update.getMessage().hasText())) {
            int totalMessageScore = 0;
            String[] wordsOfMessage = invokeWordsFromRawMessage(update.getMessage().getText());

//            MessageOperations.sendNewMessage(
//                    update.getMessage().getChatId(),
//                    update.getMessage().getText(),
//                    engine);
//
//            MessageOperations.sendNewMessage(
//                    update.getMessage().getChatId(),
//                    Arrays.toString(wordsOfMessage),
//                    engine);
//            log.warn("выслали месагу");

            List<Callable<Integer>> tasks = new ArrayList<>();
            for (String word : wordsOfMessage) {
                Search search = new Search(word, wordDictionary);
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
                String response = "Подозреваю, это спам. \"Вхождение\" сообщения в словарь банвордов - "
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
                saveMessageIntoDictionary(wordsOfMessage);
            }
        }
    }

    //TODO: вынести в отдельный сервис
    public void saveMessageIntoDictionary(String[] wordsOfMessage) {
        скопировать это в новосозданный сервис
        Arrays.stream(wordsOfMessage)
                .forEach(word -> wordDictionary.put(word, 1));
        log.warn("current dictionaty {}", wordDictionary);
    }

    //TODO: вынести в отдельный сервис
    public String[] invokeWordsFromRawMessage(String incomeMessage) {
        incomeMessage = incomeMessage
                .toLowerCase()
                .replaceAll("\\n", " ")
                .replaceAll("[\\p{So}\\p{Cn}]", "emoji")
                .trim();
        return incomeMessage.split(" ");
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


//                final CallbackDataSender[][] yesNoButtons = {{
//                        new CallbackDataSender("Yes", new CallbackData("judgement", "y " + update.getMessage().getFrom().getId())),
//                        new CallbackDataSender("No", new CallbackData("judgement", "n "))
//                }};
//                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
//                List<InlineKeyboardButton> row = new ArrayList<>();
//                InlineKeyboardButton button = new InlineKeyboardButton();
//                button.setText("Да");
//                button.setCallbackData("{\"command\":\"judgement\", \"data\":\"y "+ update.getMessage().getFrom().getId() +"\"}");
//                row.add(button);
//                button = new InlineKeyboardButton();
//                button.setText("Нет");
//                button.setCallbackData("{\"command\":\"judgement\", \"data\":\"n "+ update.getMessage().getFrom().getId() +"\"}");
//                row.add(button);
//                rowsInline.add(row);
//                inlineKeyboardMarkup.setKeyboard(rowsInline);

//                UserBotSession userBotSession = new UserBotSession(
//                        "judgement",
//                        TypeCommand.CALLBACK,
//                        260113861l
//                );
}
