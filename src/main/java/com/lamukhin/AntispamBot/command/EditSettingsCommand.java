package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.algorithm.SearchSettings;
import com.lamukhin.AntispamBot.util.MessageOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;
import ru.wdeath.managerbot.lib.bot.annotations.CommandFirst;
import ru.wdeath.managerbot.lib.bot.annotations.CommandNames;
import ru.wdeath.managerbot.lib.bot.annotations.CommandOther;
import ru.wdeath.managerbot.lib.bot.annotations.ParamName;
import ru.wdeath.managerbot.lib.bot.command.CommandContext;
import ru.wdeath.managerbot.lib.bot.command.TypeCommand;
import ru.wdeath.managerbot.lib.bot.session.UserBotSession;

import java.util.Arrays;

@Component
@CommandNames(value = EditSettingsCommand.NAME, type = TypeCommand.CALLBACK)
public class EditSettingsCommand {
    public static final String NAME = "edit-settings";
    private final Logger log = LoggerFactory.getLogger(EditSettingsCommand.class);
    private final SearchSettings searchSettings;

    @CommandFirst
    public void showSettings(TelegramLongPollingEngine engine,
                             @ParamName("chatId") Long chatId,
                             CommandContext context,
                             UserBotSession userBotSession) {
        if (chatId == 260113861L) {
            String data = (String) context.getData();
            switch (data) {
                case "short" -> {
                    MessageOperations.sendNewMessage(
                            chatId,
                            "Пришлите новые значения для коротких сообщений через пробел в формате 5 10 0.5",
                            engine
                    );
                    userBotSession.setData("short");
                }
                case "middle" -> {
                    MessageOperations.sendNewMessage(
                            chatId,
                            "Пришлите новые значения для средних сообщений через пробел в формате 5 10 0.5",
                            engine
                    );
                    userBotSession.setData("middle");
                }
                case "long" -> {
                    MessageOperations.sendNewMessage(
                            chatId,
                            "Пришлите новые значения для длинных сообщений через пробел в формате 5 10 0.5",
                            engine
                    );
                    userBotSession.setData("long");
                }
                case "word" -> {
                    MessageOperations.sendNewMessage(
                            chatId,
                            "Пришлите новое значение кэфа для одного слова в формате 0.5",
                            engine
                    );
                    userBotSession.setData("word");
                }
            }
        }
    }

    //TODO: пока в разработке
    @CommandOther
    public void changeData(
            TelegramLongPollingEngine engine,
            @ParamName("chatId") Long chatId,
            Update update,
            UserBotSession userBotSession,
            CommandContext context) {
        String newMessage = context.getUpdate().getMessage().getText();
        log.warn("текст месаги {}", newMessage);
        String[] values = newMessage.split(" ");
        Arrays.asList(values).forEach(System.out::println);
        switch ((String) userBotSession.getData()) {
            case "short" -> {
                searchSettings.getSegmentForShort().setStart(Integer.parseInt(values[0]));
                searchSettings.getSegmentForShort().setEnd(Integer.parseInt(values[1]));
                searchSettings.setCoefForShortMessage(Double.parseDouble(values[2]));
                MessageOperations.sendNewMessage(
                        chatId,
                        "Данные для коротких сообщений сохранены. /settings, чтобы убедиться",
                        engine
                );
            }
            case "middle" -> {
                searchSettings.getSegmentForMiddle().setStart(Integer.parseInt(values[0]));
                searchSettings.getSegmentForMiddle().setEnd(Integer.parseInt(values[1]));
                searchSettings.setCoefForMiddleMessage(Double.parseDouble(values[2]));
                MessageOperations.sendNewMessage(
                        chatId,
                        "Данные для средних сообщений сохранены. /settings, чтобы убедиться",
                        engine
                );
            }
            case "long" -> {
                searchSettings.getSegmentForLong().setStart(Integer.parseInt(values[0]));
                searchSettings.getSegmentForLong().setEnd(Integer.parseInt(values[1]));
                searchSettings.setCoefForLongMessage(Double.parseDouble(values[2]));
                MessageOperations.sendNewMessage(
                        chatId,
                        "Данные для длинных сообщений сохранены. /settings, чтобы убедиться",
                        engine
                );
            }
            case "word" -> {
                searchSettings.setCoefForCurrentWord(Double.parseDouble(values[0]));
                MessageOperations.sendNewMessage(
                        chatId,
                        "Кэф для каждого слова изменён. /settings, чтобы убедиться",
                        engine
                );
            }
        }
        userBotSession.stop();
    }

    public EditSettingsCommand(SearchSettings searchSettings) {
        this.searchSettings = searchSettings;
    }
}
