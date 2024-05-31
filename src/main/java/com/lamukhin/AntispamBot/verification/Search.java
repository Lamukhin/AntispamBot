package com.lamukhin.AntispamBot.verification;

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
    private final Map<String, Integer> wordDictionary;
    private final Map<Character, Integer> symbolsDictionary;

    @Value("${coefficients.of_current_word}")
    private double forCurrentWord;

    public Search(String currentWord, Map<String, Integer> dictionary, Map<Character, Integer> symbolsDictionary) {
        this.currentWord = currentWord;
        this.wordDictionary = dictionary;
        this.symbolsDictionary = symbolsDictionary;
    }

    @Override
    public Integer call() {
        try {
            //search for banned symbols
            if (currentWord.length() < 3){ //словом считается строка от 3 символов, нам не нужны предлоги
                for (char ch : currentWord.toCharArray()){
                    if(symbolsDictionary.containsKey(ch)){
                        return symbolsDictionary.get(ch);
                    }
                }
            }
            //search for banned words
            for (String wordInDictionary : wordDictionary.keySet()) {
                //we don't compare words if their length's delta is more than 3 chars
                if (Math.abs(currentWord.length() - wordInDictionary.length()) > 3) {
                    log.warn("дельта ту биг");
                    break;
                }

                double coefOfCurrentWord = twoWordsCrossesCoef(currentWord, wordInDictionary);
                log.warn("кэф найденного слова {}", coefOfCurrentWord);

                if (coefOfCurrentWord > forCurrentWord) {
                    //if a found word EXISTS in our dictionary, we return its value.
                    // otherwise we return just 1, which means that found similar word
                    return coefOfCurrentWord == 1.0 ? wordDictionary.get(wordInDictionary) : 1;
                }
            }
            log.warn("The word \"{}\" has not found in our dictionary", currentWord);
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
                    log.warn("найдено совпадение символа {} в слове {}", currentCharInWord, currentWord);
                    crosses.add(currentCharInWord);
                    inWordCrossesCounter++;
                }
            }
        }

        double coefOfCurrentWord = (double)
                ((inWordCrossesCounter/ currentWord.length())+(inWordCrossesCounter/wordInDictionary.length()))
                / 2;

        return  coefOfCurrentWord;
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
