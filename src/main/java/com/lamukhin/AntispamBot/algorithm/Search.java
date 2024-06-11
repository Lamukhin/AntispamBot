package com.lamukhin.AntispamBot.algorithm;

import com.lamukhin.AntispamBot.db.entity.DictionaryEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.concurrent.Callable;

/*
    This Callable class contains the main method of checking if a current word is banned or not.
 */

public class Search implements Callable<Integer> {

    private final Logger log = LoggerFactory.getLogger(Search.class);
    private final String currentWord;
    private final Map<String, DictionaryEntity> wordDictionary;

    private final SearchSettings searchSettings;

    public Search(String currentWord, Map<String, DictionaryEntity> dictionary, SearchSettings searchSettings) {
        this.currentWord = currentWord;
        this.wordDictionary = dictionary;
        this.searchSettings = searchSettings;
    }

    @Override
    public Integer call() {
        try {
            //search for banned symbols and emoji
            if(currentWord.contains("emoji")){
                return 1;
            }
            //search for banned words
            if (currentWord.length() >= 3) { //a word is considered a string of at least 3 characters. we don't consider "prepositions"
                for (String wordInDictionary : wordDictionary.keySet()) {
                    //we don't compare words if their length's delta is more than 3 chars
                    if (Math.abs(currentWord.length() - wordInDictionary.length()) > 3) {
                        //log.warn("The delta of words is too big for a comparison. {} {}",currentWord,wordInDictionary);
                        continue;
                    }

                    double coefOfCurrentWord = twoWordsCrossesCoef(currentWord, wordInDictionary);
                    log.warn("Coefficient of the current words: {} : \"{}\" and \"{}\"", coefOfCurrentWord, currentWord, wordInDictionary);

                    if (coefOfCurrentWord > searchSettings.getCoefForCurrentWord()) {
                        //if a found word EXISTS in our dictionary, we return its value.
                        // otherwise we return just 1, which means that found similar word
                        return coefOfCurrentWord == 1.0 ? wordDictionary.get(wordInDictionary).getValue() : 1;
                    }
                }
            }
            //log.warn("The word \"{}\" has not found in our dictionary", currentWord);
            return 0;
        } catch (Exception ex) {
            log.error("Exception inside CALL method: {}", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private double twoWordsCrossesCoef(String currentWord, String wordInDictionary) {
        List<Character> crosses = new ArrayList<>();
        int inWordCrossesCounter = 0;
        for (char currentCharInWord : currentWord.toCharArray()) {
            if (!crosses.contains(currentCharInWord)) {
                int amountAtWord = countItem(currentWord.toCharArray(), currentCharInWord);
                int amountAtDictionary = countItem(wordInDictionary.toCharArray(), currentCharInWord);
                int minCount = Math.min(amountAtWord, amountAtDictionary);
                for (int i = 0; i < minCount; i++) {
                    //log.warn("Matched \'{}\' has found in word \"{}\"", currentCharInWord, currentWord);
                    crosses.add(currentCharInWord);
                    inWordCrossesCounter++;
                }
            }
        }

        return  (double)
                ((inWordCrossesCounter/ currentWord.length())+(inWordCrossesCounter/wordInDictionary.length()))
                / 2;

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