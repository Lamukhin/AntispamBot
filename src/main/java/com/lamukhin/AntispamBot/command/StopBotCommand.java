package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.listener.CustomUpdateListener;
import com.lamukhin.AntispamBot.role.Admins;
import com.lamukhin.AntispamBot.util.MessageOperations;
import org.springframework.stereotype.Component;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;
import ru.wdeath.managerbot.lib.bot.annotations.CommandFirst;
import ru.wdeath.managerbot.lib.bot.annotations.CommandNames;
import ru.wdeath.managerbot.lib.bot.annotations.ParamName;
import ru.wdeath.managerbot.lib.bot.command.TypeCommand;

@Component
@CommandNames(value = StopBotCommand.NAME, type = TypeCommand.MESSAGE)
public class StopBotCommand {

    public static final String NAME = "/stop_bot";
    private final Admins admins;
    private final CustomUpdateListener customUpdateListener;

    @CommandFirst
    public void stopBot(TelegramLongPollingEngine engine,
                         @ParamName("chatId") Long chatId,
                         @ParamName("userId") Long userId){

         if (admins.getSet().contains(String.valueOf(userId))){
             customUpdateListener.setPaused(true);
             MessageOperations.sendNewMessage(
                     chatId,
                     "Бот приостановлен.",
                     engine
             );
         }

    }

    public StopBotCommand(Admins admins, CustomUpdateListener customUpdateListener) {
        this.admins = admins;
        this.customUpdateListener = customUpdateListener;
    }
}
