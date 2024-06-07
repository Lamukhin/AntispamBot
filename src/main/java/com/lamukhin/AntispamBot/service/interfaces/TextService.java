package com.lamukhin.AntispamBot.service.interfaces;

public interface TextService {

    String[] invokeWordsFromRawMessage(String incomeMessage);

    void saveMessageIntoDictionary(String[] wordsOfMessage);
}
