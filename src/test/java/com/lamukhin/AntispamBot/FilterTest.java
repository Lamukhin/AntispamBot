package com.lamukhin.AntispamBot;

import com.lamukhin.AntispamBot.service.impl.SpamCheckingServiceSecond;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
public class FilterTest {

    @Autowired
    SpamCheckingServiceSecond spamCheckingServiceSecond;

    @Test
    void checkUpdateTest() {

        String incomeMessage = "\u2060\u2060\u2060\u2060\u2060\u2060\u2060\u2060\u2060\u2060❗\uFE0FOcтopoжнo❗\uFE0F Нужeн человek  удaлеnnого сотpyдничества \n" +
                "\n" +
                "- с нac бeсплатное обучeние, c тебя забираeм прoцент с кaждой нашей выполненной сделки\n" +
                "- пpu6ыль от 1000 $ неделя/белaя тема.\n" +
                "- вход строго +21\n" +
                "- работаешь на личном счету, поэтoму для тебя все прoзрaчно и яcнo\n" +
                "\n" +
                "Если ты хочешь, чтобы $ ра6отaлu на тeбя, meбе k нaм!\n" +
                "\n" +
                "За6ронuруй место сейчас !!!!\n" +
                "\n" +
                "manager13 (https://grinnlandygertyuj.site/067b68740d61a0a3708fa83989a05fef)";
        incomeMessage = incomeMessage
                .toLowerCase()
                .replaceAll("[^a-zA-Zа-яА-Я0-9\s]", " ")
                //.replaceAll("не", "")
                .trim();
        String[] wordsOfMessage = incomeMessage.split(" ");
        Arrays.stream(wordsOfMessage).forEach(e -> {
            if (e.length() > 2) {
                System.out.print("\'" + e + "\'");
            };
        });

    }
}
