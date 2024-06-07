package com.lamukhin.AntispamBot.service.interfaces;

import com.lamukhin.AntispamBot.db.entity.DictionaryEntity;

import java.util.Map;

public interface TextService {

    String[] invokeWordsFromRawMessage(String incomeMessage);
    void saveMessageIntoDictionary(String[] wordsOfMessage);
    Map<String, DictionaryEntity> getCachedDictionary();
}
