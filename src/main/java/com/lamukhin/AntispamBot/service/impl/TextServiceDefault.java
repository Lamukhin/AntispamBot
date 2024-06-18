package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.db.entity.DictionaryEntity;
import com.lamukhin.AntispamBot.db.repo.DictionaryRepo;
import com.lamukhin.AntispamBot.service.interfaces.TextService;
import com.lamukhin.AntispamBot.util.TextFiltrationProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TextServiceDefault implements TextService {

    private final Logger log = LoggerFactory.getLogger(TextServiceDefault.class);
    //TODO: посмотреть, насколько тяжело будет оперативе. если не потянет,
    // то надо брать из бд каждый раз
    private Map<String, DictionaryEntity> cachedDictionary = new HashMap<>();
    private final DictionaryRepo dictionaryRepo;

    @PostConstruct
    private void fetchDictionaryFromDatabase() {
        List<DictionaryEntity> result = dictionaryRepo.findAll();
        if (result.isEmpty()) {
            log.warn("Dictionary is empty in Database, cache is empty now too.");
        } else {
            result.forEach(element ->
                    cachedDictionary.put(
                            String.valueOf(element.getWord()),
                            element
                    )
            );
            log.warn("Dictionary has successfully fetched from the Database, {} records found.", cachedDictionary.size());
        }
    }

    @Override
    public String[] invokeWordsFromRawMessage(String incomeMessage, TextFiltrationProps textFiltrationProps) {
        incomeMessage = incomeMessage
                .toLowerCase()
                .replaceAll("\\n", " ")
                .replaceAll("\\p{Punct}", " ")
                .replaceAll("[\\p{So}\\p{Cn}]", "emoji")
                .trim();
        switch (textFiltrationProps) {
            case NO_SHORTS -> {
                return Arrays
                        .stream(incomeMessage.split(" "))
                        .filter(word -> (word.length() >= 3))
                        .toArray(String[]::new);
            }
            default -> {
                return Arrays
                        .stream(incomeMessage.split(" "))
                        .filter(word -> (word.length() > 1)) // word.length() == 0 causes npe at division
                        .toArray(String[]::new);
            }
        }
    }

    @Override
    public void saveMessageIntoDictionary(String[] wordsOfMessage) {
        Arrays.stream(wordsOfMessage)
                .forEach(word -> {
                    DictionaryEntity currentNewWord = new DictionaryEntity(word, 1);
                    if (cachedDictionary.putIfAbsent(word, currentNewWord) == null) {
                        dictionaryRepo.save(currentNewWord);
                    }
                });
        log.warn("The dictionary has been updated");
    }

    @Override
    public Map<String, DictionaryEntity> getCachedDictionary() {
        return cachedDictionary;
    }

    public TextServiceDefault(DictionaryRepo dictionaryRepo) {
        this.dictionaryRepo = dictionaryRepo;
    }
}
