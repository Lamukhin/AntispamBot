package com.lamukhin.AntispamBot;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.event.KeyEvent;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class SearchAlgorithmTest {

    private final Logger log = LoggerFactory.getLogger(SearchAlgorithmTest.class);


    @Test
    public void twoWordsCrossesCoef() {
        String currentWord = "работать"; //список пересечений [б, т, ь]
        String wordInDictionary = "зapaбoтaeшь";
        List<Character> crosses = new ArrayList<>();
        int inWordCrossesCounter = 0;
        for (char currentCharInWord : currentWord.toCharArray()) {
            log.warn("Очередная буква {} в словe {}", currentCharInWord, currentWord);
            if (!crosses.contains(currentCharInWord)) {
                log.warn("ранее не было в пересечениях");
                int amountAtWord = countItemWithHash(currentWord.toCharArray(), currentCharInWord);
                log.warn("{} в словe {} встречается {} раз", currentCharInWord, currentWord, amountAtWord);
                int amountAtDictionary = countItemWithHash(wordInDictionary.toCharArray(), currentCharInWord);
                log.warn("{} в словe {} встречается {} раз", currentCharInWord, wordInDictionary, amountAtDictionary);
                int minCount = Math.min(amountAtWord, amountAtDictionary);
                for (int i = 0; i < minCount; i++) {
                    log.warn("добавление в пересечения {}", currentCharInWord);
                    crosses.add(currentCharInWord);
                    inWordCrossesCounter++;
                }
            } else {
                log.warn("ранее было в пересечениях, пропуск");
            }
        }
        log.warn("счётчик пересечений {}", inWordCrossesCounter);
        log.warn("список пересечений {}", crosses);

        double result = (double)
                ((inWordCrossesCounter / currentWord.length()) + (inWordCrossesCounter / wordInDictionary.length()))
                / 2;
        log.warn("RESULT COEF {}", result);

    }

    @Test
    public void countItemTest() {
        String wordInDictionary = "зapaбoтaeшь";
        char currentCharInWord = 'р';
        int amountAtDictionary = countItemNormalized(wordInDictionary.toCharArray(), currentCharInWord);
        log.warn("result {}", amountAtDictionary);
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
    private int countItemWithHash(char[] list, char item) {
        int count = 0;
        int current;
        int searching = String.valueOf(item).hashCode();
        log.warn("searching hash {}", searching );
        for (char element : list) {
            current = String.valueOf(element).hashCode();
            log.warn("current hash {}", current );
            if (current == searching) {
                count++;
            }
        }
        return count;
    }
    private int countItemNormalized(char[] wordAsChars, char searchingChar) {
        //log.warn("зашли в countItem");
        int count = 0;
        for (char currentChar : wordAsChars) {
            //log.warn("элемент в массиве {}, искомый элемент {}", element, item);
            String normalizedCurrentChar = Normalizer.normalize(String.valueOf(currentChar), Normalizer.Form.NFD);
            String normalizedSearchingChar = Normalizer.normalize(String.valueOf(searchingChar), Normalizer.Form.NFD);
            if (normalizedCurrentChar.equalsIgnoreCase(normalizedSearchingChar)) {
                log.warn("нашли символ в массиве");
                count++;
            }
        }
        //log.warn("возвращаем счетчик итоговый {}", count);
        return count;
    }

    @Test
    public void charsTest() {
        char russian = 'p';
        char english = 'р';

        //неравны
        String russianModifiersExText = KeyEvent.getModifiersExText(russian);
        String englishModifiersExText = KeyEvent.getModifiersExText(english);

        //неравны
        String russianKeyText = KeyEvent.getKeyText(russian);
        String englishKeyText = KeyEvent.getKeyText(english);

        //неравны
        int russianKeyCode = KeyEvent.getExtendedKeyCodeForChar(russian);
        int englishKeyCode = KeyEvent.getExtendedKeyCodeForChar(english);

        log.warn("rus {}", russianKeyText);
        log.warn("eng {}", englishKeyText);

        if (russianKeyText.equals(englishKeyText)) {
            log.warn("same");
        } else {
            log.warn("different");
        }

//        if (russianKeyCode == englishKeyCode) {
//            log.warn("same");
//        } else {
//            log.warn("different");
//        }
    }
}
