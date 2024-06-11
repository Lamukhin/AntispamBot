package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.db.entity.DictionaryEntity;
import com.lamukhin.AntispamBot.db.entity.MessageCountEntity;
import com.lamukhin.AntispamBot.db.repo.DictionaryRepo;
import com.lamukhin.AntispamBot.db.repo.MessageCountRepo;
import com.lamukhin.AntispamBot.service.interfaces.TextService;
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
    private void fetchDictionaryFromDatabase(){
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
                .forEach(word -> {
                    DictionaryEntity currentNewWord = new DictionaryEntity(word, 1);
                    if (cachedDictionary.putIfAbsent(word, currentNewWord) == null){
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
