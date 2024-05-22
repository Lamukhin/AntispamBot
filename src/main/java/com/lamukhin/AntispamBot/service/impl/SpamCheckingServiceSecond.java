package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.service.interfaces.SpamCheckingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SpamCheckingServiceSecond implements SpamCheckingService {

    private final Logger log = LoggerFactory.getLogger(SpamCheckingServiceDefault.class);
    private Set<String> dictionary = new HashSet<>();

    @Override
    public void checkUpdate(Update update, TelegramLongPollingEngine engine) {
        dictionary.add("sex");
        if ((update.hasMessage()) && (update.getMessage().hasText())) {
            String incomeMessage = update.getMessage().getText();
            incomeMessage = incomeMessage.replaceAll("[^a-zA-Zа-яА-Я0-9\s]", "").trim();
            String[] wordsOfMessage = incomeMessage.split(" ");
            List<String> pairsOfWords = new ArrayList<>();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < wordsOfMessage.length; i++) {
                stringBuilder
                        .append(wordsOfMessage[i])
                        .append(" ")
                        .append(wordsOfMessage[i + 1]);
                pairsOfWords.add(stringBuilder.toString());
                stringBuilder = new StringBuilder();
            }
            double coef = 0.0;
            int counter = 0;
            List<Character> crosses = new ArrayList<>();
            for (String pair : pairsOfWords) {
                for (String wordInDictionary : dictionary) {
                    int inWordCrossesCounter = 0;
                    for (char currentCharInPair : pair.toCharArray()) {
                        if (!crosses.contains(currentCharInPair)) {
                            int amountAtPair = countItem(pair.toCharArray(), currentCharInPair);
                            int amountAtDictionary = countItem(wordInDictionary.toCharArray(), currentCharInPair);
                            int minCount = Math.min(amountAtPair, amountAtDictionary);
                            for (int i = 0; i < minCount; i++) {
                                crosses.add(currentCharInPair);
                                inWordCrossesCounter++;
                            }
                        }
                    }
                    coef = (double) inWordCrossesCounter / ());
                    вот тут возникает проблема, как выводить кэф?
                    наверное лучше не парами тогда а по слову и фильтровать по длине
                            например если это предлагаю то однокоренные будут рядом и сравнятся с
                            высоким кэфом
                    if (coef > 0.7)
                }
            }

        }
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
