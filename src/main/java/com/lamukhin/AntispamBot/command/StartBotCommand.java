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
@CommandNames(value = StartBotCommand.NAME, type = TypeCommand.MESSAGE)
public class StartBotCommand {

    public static final String NAME = "/start_bot";
    private final Admins admins;
    private final CustomUpdateListener customUpdateListener;

    @CommandFirst
    public void startBot(TelegramLongPollingEngine engine,
                         @ParamName("chatId") Long chatId,
                         @ParamName("userId") Long userId){

         if (admins.getSet().contains(String.valueOf(userId))){
             customUpdateListener.setPaused(false);
             MessageOperations.sendNewMessage(
                     chatId,
                     "Бот запущен",
                     engine
             );
         }

    }

    public StartBotCommand(Admins admins, CustomUpdateListener customUpdateListener) {
        this.admins = admins;
        this.customUpdateListener = customUpdateListener;
    }
}
