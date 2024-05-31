package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.util.MessageOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;
import ru.wdeath.managerbot.lib.bot.annotations.CommandFirst;
import ru.wdeath.managerbot.lib.bot.annotations.CommandNames;
import ru.wdeath.managerbot.lib.bot.annotations.CommandOther;
import ru.wdeath.managerbot.lib.bot.annotations.ParamName;
import ru.wdeath.managerbot.lib.bot.command.CommandContext;
import ru.wdeath.managerbot.lib.bot.session.UserBotSession;

import java.sql.Timestamp;

import static com.lamukhin.AntispamBot.util.ResponseMessage.*;

@Component
@CommandNames("/ping_bot")
public class StartCommand {

    private final Logger log = LoggerFactory.getLogger(StartCommand.class);
    private long helloTime = System.currentTimeMillis();
    private boolean justStarted = true;
    private boolean floodCatched = false;

    @CommandFirst
    public void greeting(TelegramLongPollingEngine engine,
                         @ParamName("chatId") Long chatId) {
        if (System.currentTimeMillis() - helloTime < 1800000) {
            if (justStarted) {
                MessageOperations.sendNewMessage(
                        chatId,
                        HELLO,
                        engine);
                helloTime = System.currentTimeMillis();
                justStarted = false;
            } else if (!floodCatched) {
                MessageOperations.sendNewMessage(
                        chatId,
                        DONT_FLOOD,
                        engine);
                floodCatched = true;
            }
        } else {
            MessageOperations.sendNewMessage(
                    chatId,
                    HELLO,
                    engine);
            helloTime = System.currentTimeMillis();
            floodCatched = true;
        }

    }

}
