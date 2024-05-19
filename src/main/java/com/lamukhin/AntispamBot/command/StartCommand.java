package com.lamukhin.AntispamBot.command;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;
import ru.wdeath.managerbot.lib.bot.annotations.CommandFirst;
import ru.wdeath.managerbot.lib.bot.annotations.CommandNames;
import ru.wdeath.managerbot.lib.bot.annotations.ParamName;

import java.util.ArrayList;
import java.util.List;

@Component
@CommandNames("/start")
public class StartCommand {

    private final Logger log = LoggerFactory.getLogger(StartCommand.class);

    @CommandFirst
    public void response(TelegramLongPollingEngine engine,
                         @ParamName("chatId") Long chatId,
                         Update update) {
        log.warn("krolik", update.getMessage().getChat());
        var send = new SendMessage();
        send.setChatId(chatId);
        send.setText("hellow");try {
            engine.execute(send);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
