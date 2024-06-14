package com.lamukhin.AntispamBot.service.interfaces;

import com.lamukhin.AntispamBot.db.entity.DictionaryEntity;
import com.lamukhin.AntispamBot.util.TextFiltrationProps;

import java.util.Map;

public interface TextService {


    String[] invokeWordsFromRawMessage(String incomeMessage, TextFiltrationProps messageFilterType);

    void saveMessageIntoDictionary(String[] wordsOfMessage);

    Map<String, DictionaryEntity> getCachedDictionary();
}
