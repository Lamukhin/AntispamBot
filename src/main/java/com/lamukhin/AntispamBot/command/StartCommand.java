package com.lamukhin.AntispamBot.command;

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

    @CommandFirst
    public void greeting(TelegramLongPollingEngine engine,
                         UserBotSession userBotSession,
                         @ParamName("chatId") Long chatId) {
        if (System.currentTimeMillis() - helloTime < 1800000) {
            if (justStarted) {
                var send = new SendMessage();
                send.setChatId(chatId);
                send.setText("ПРИВЕТ");
                //TODO: внедрить утилитный класс для отправки сообщений
                try {
                    engine.execute(send);
                    helloTime = System.currentTimeMillis();
                    justStarted = false;
                } catch (TelegramApiException e) {
                    log.error(e.getMessage());
                }
            } else {
                var send = new SendMessage();
                send.setChatId(chatId);
                send.setText("РАНО");
                //TODO: внедрить утилитный класс для отправки сообщений
                try {
                    engine.execute(send);
                    helloTime = System.currentTimeMillis();
                } catch (TelegramApiException e) {
                    log.error(e.getMessage());
                }
            }
        } else {
            var send = new SendMessage();
            send.setChatId(chatId);
            send.setText(HELLO);
            //TODO: внедрить утилитный класс для отправки сообщений
            try {
                engine.execute(send);
                helloTime = System.currentTimeMillis();
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }

    }

    @CommandOther
    public void floodCheck(TelegramLongPollingEngine engine,
                           UserBotSession userBotSession,
                           @ParamName("chatId") Long chatId) {
        long helloTime = (Long) userBotSession.getData();
        if (System.currentTimeMillis() - helloTime < 1800000) {
            var send = new SendMessage();
            send.setChatId(chatId);
            send.setText("Не нужно флудить");
            try {
                engine.execute(send);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        } else {
            var send = new SendMessage();
            send.setChatId(chatId);
            send.setText("hello     ");
            try {
                engine.execute(send);
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
        }


    }
}
