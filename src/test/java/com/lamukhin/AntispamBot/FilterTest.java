package com.lamukhin.AntispamBot;

import com.lamukhin.AntispamBot.service.impl.SpamCheckingServiceSecond;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
public class FilterTest {


    @Test
    void messageFilterTest() {

//        String incomeMessage = "\u2060\u2060\u2060\u2060\u2060\u2060\u2060\u2060\u2060\u2060❗\uFE0FOcтopoжнo❗\uFE0F Нужeн человek  удaлеnnого сотpyдничества \n" +
//                "\n" +
//                "- с нac бeсплатное обучeние, c тебя забираeм прoцент с кaждой нашей выполненной сделки\n" +
//                "- пpu6ыль от 1000 $ неделя/белaя тема.\n" +
//                "- вход строго +21\n" +
//                "- работаешь на личном счету, поэтoму для тебя все прoзрaчно и яcнo\n" +
//                "\n" +
//                "Если ты хочешь, чтобы $ ра6отaлu на тeбя, meбе k нaм!\n" +
//                "\n" +
//                "За6ронuруй место сейчас !!!!\n" +
//                "\n" +
//                "manager13 (https://grinnlandygertyuj.site/067b68740d61a0a3708fa83989a05fef)";
        String incomeMessage ="\uD83D\uDD24\uD83D\uDD24\uD83D\uDD24\uD83D\uDD24\uD83D\uDD24\uD83D\uDD24\uD83D\uDD24\uD83D\uDD24\n" +
                "Дoбpый вeчep. \uD83D\uDC4F\n" +
                "В aктивнoм пoиcкe людeй кoтopым интepeceн coвмecтный зapaбoтoк и дoпoлнитeльный дoxoд. \uD83D\uDCB0\n" +
                "В пpиopитeтe дoлгoe и чecтнoe coтpyдничecтвo, cфepa: цифpoвыx aктивoв, вceмy oбyчим c пoлнoгo 0. ✅\n" +
                "Пo пpиpocтy пpибыли в дeнь paвнa 4-7% к oбщим cpeдcтвaм. \uD83D\uDCBC\n" +
                "Ecли интepecyeт тo пишитe нa мoй ocнoвнoй aккayнт: @DMITRY3191.";
//        incomeMessage = incomeMessage
//                .toLowerCase()
//                .replaceAll("[^a-zA-Zа-яА-Я0-9\s]", " ")
//                //.replaceAll("не", "")
//                .trim();
        incomeMessage = incomeMessage
                .toLowerCase()
                .replaceAll("\\n", " ")
                .replaceAll("[\\p{So}\\p{Cn}]", "ПИЗДА")
                .trim();
        String[] wordsOfMessage = incomeMessage.split(" ");
        System.out.println(incomeMessage);
        System.out.println();
        Arrays.stream(wordsOfMessage).forEach(e -> {
            //if (e.length() > 2) {
                System.out.print("\'" + e + "\'");
            //};
        });

    }
}
