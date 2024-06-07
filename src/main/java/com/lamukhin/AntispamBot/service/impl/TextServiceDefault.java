package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.service.interfaces.TextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class TextServiceDefault implements TextService {

    private final Logger log = LoggerFactory.getLogger(TextServiceDefault.class);
    private Map<String, Integer> cachedDictionary = new HashMap<>();
    @Override
    public String[] invokeWordsFromRawMessage(String incomeMessage) {
        incomeMessage = incomeMessage
                .toLowerCase()
                .replaceAll("\\n", " ")
                .replaceAll("[\\p{So}\\p{Cn}]", "emoji")
                .trim();
        return incomeMessage.split(" ");
    }

    @Override
    public void saveMessageIntoDictionary(String[] wordsOfMessage) {
        Arrays.stream(wordsOfMessage)
                .forEach(word -> cachedDictionary.put(word, 1));
        log.warn("current dictionaty {}", cachedDictionary);
    }
}
