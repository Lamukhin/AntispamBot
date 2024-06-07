package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.service.interfaces.TextService;

public class TextServiceDefault implements TextService {
    @Override
    public String[] invokeWordsFromRawMessage(String incomeMessage) {
        return new String[0];
    }

    @Override
    public void saveMessageIntoDictionary(String[] wordsOfMessage) {

    }
}
