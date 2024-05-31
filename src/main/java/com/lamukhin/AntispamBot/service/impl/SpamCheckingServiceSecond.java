package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.service.interfaces.SpamCheckingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;

import java.util.*;

//@Service
public class SpamCheckingServiceSecond implements SpamCheckingService {

    private final Logger log = LoggerFactory.getLogger(SpamCheckingServiceDefault.class);
    private Map<String, Integer> dictionary = new HashMap<>();

    @Value("${coefficients.of_current_word}")
    private double forCurrentWord;
    @Value("${coefficients.for_4_to_6_length}")
    private double for4To6Length;
    @Value("${coefficients.for_7_to_20_length}")
    private double for7To20Length;
    @Value("${coefficients.for_more_21_length}")
    private double forMoreThan21Length;

    @Override
    public void checkUpdate(Update update, TelegramLongPollingEngine engine) {
        dictionary.put("sex", 1);
        Set<Character> banSymbols = new HashSet<>();
        banSymbols.add('@');
        banSymbols.add('+');
        banSymbols.add('$');
        if ((update.hasMessage()) && (update.getMessage().hasText())) {
            String incomeMessage = update.getMessage().getText();
            incomeMessage = incomeMessage
                    .toLowerCase()
                    .replaceAll("[^a-zA-Zа-яА-Я0-9\s]", " ")
                    //.replaceAll("не", "")
                    .trim();
            String[] wordsOfMessage = incomeMessage.split(" ");
            Arrays.stream(wordsOfMessage).forEach(e -> System.out.print(e + " "));
            //Вариант с парными словами пока отложим
//            List<String> pairsOfWords = new ArrayList<>();
//            StringBuilder stringBuilder = new StringBuilder();
//            for (int i = 0; i < wordsOfMessage.length; i++) {
//                stringBuilder
//                        .append(wordsOfMessage[i])
//                        .append(" ")
//                        .append(wordsOfMessage[i + 1]);
//                pairsOfWords.add(stringBuilder.toString());
//                stringBuilder = new StringBuilder();
//            }
            //double coef = 0.0;
            int totalMessageScore = 0;

            for (String word : wordsOfMessage) {
                if (word.length() < 3){ //словом считается строка от 3 символов, нам не нужны предлоги
                    if ((banSymbols.contains(word.charAt(0)))||(banSymbols.contains(word.charAt(1)))){
                        //некоторые символы просто нельзя игнорировать. например, $ или +
                        totalMessageScore++;
                    }
                    break;
                }
                for (String wordInDictionary : dictionary.keySet()) {
                    if (Math.abs(word.length() - wordInDictionary.length()) > 3) {
                        System.out.println("дельта ту биг");
                        break; // если дельта длины слов больше 3, то слишком сложно сравнить достоверно
                    }
                    List<Character> crosses = new ArrayList<>();
                    int inWordCrossesCounter = 0;
                    for (char currentCharInWord : word.toCharArray()) {
                        if (!crosses.contains(currentCharInWord)) {
                            int amountAtWord = countItem(word.toCharArray(), currentCharInWord);
                            int amountAtDictionary = countItem(wordInDictionary.toCharArray(), currentCharInWord);
                            int minCount = Math.min(amountAtWord, amountAtDictionary);
                            for (int i = 0; i < minCount; i++) {
                                log.warn("найдено совпадение символа {} в слове {}", currentCharInWord, word);
                                crosses.add(currentCharInWord);
                                inWordCrossesCounter++;
                            }
                        }
                    }
                    double coefOfCurrentWord = (double)
                            ((inWordCrossesCounter/word.length())+(inWordCrossesCounter/wordInDictionary.length()))
                            / 2;
                    log.warn("кэф найденного слова {}", coefOfCurrentWord);
                    if (coefOfCurrentWord > forCurrentWord) {
                        totalMessageScore += dictionary.get(wordInDictionary);
                    }
                }

            }
            double coefOfAllMessage = (double) totalMessageScore / wordsOfMessage.length;
            if (isSpam(coefOfAllMessage, wordsOfMessage.length)){
                System.out.println("IT IS SPAM");
                var send = DeleteMessage.builder()
                        .chatId(update.getMessage().getChatId())
                        .messageId(update.getMessage().getMessageId())
                        .build();
                engine.executeNotException(send);
            }
            System.out.println("IT IS NOT SPAM");

        }
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

    private int countItem(char[] list, char item) {
        int count = 0;
        for (char element : list) {
            if (element == item) {
                count++;
            }
        }
        return count;
    }

}
