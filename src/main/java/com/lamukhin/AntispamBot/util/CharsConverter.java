package com.lamukhin.AntispamBot.util;

import java.util.HashMap;

@Deprecated
//эту же функцию спокойно выполняет StringUtils.replaceChars
public class CharsConverter {
    private final static HashMap<Character, Character> charsMap = new HashMap<>() {
        {
            put('a', 'а');
            put('e', 'е');
            put('k', 'к');
            put('m', 'м');
            put('o', 'о');
            put('c', 'с');
            put('t', 'т');
            put('b', 'в');
            put('h', 'н');
            put('p', 'р');
            put('x', 'ч');
            put('y', 'у');
        }
    };

    //А, Е, К, М, О, С, Т, В, Н, Р, Х, Y
    //a e k m o c t b h p x y

    public static char[] allSameLatinsToCyrillic(String word) {
        char[] wordAsArray = word.toCharArray();
        for (int i = 0; i < wordAsArray.length; i++) {
            if (charsMap.containsKey(wordAsArray[i])) {
                wordAsArray[i] = charsMap.get(wordAsArray[i]);
            }
        }
        return wordAsArray;
    }
}
