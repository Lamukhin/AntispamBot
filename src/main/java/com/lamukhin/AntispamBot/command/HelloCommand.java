package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.util.MessageOperations;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;
import ru.wdeath.managerbot.lib.bot.annotations.CommandFirst;
import ru.wdeath.managerbot.lib.bot.annotations.CommandNames;
import ru.wdeath.managerbot.lib.bot.annotations.ParamName;
import ru.wdeath.managerbot.lib.bot.command.CommandContext;
import ru.wdeath.managerbot.lib.bot.command.TypeCommand;

import java.util.ArrayList;
import java.util.List;

@Component
@CommandNames(value = HelloCommand.NAME, type = TypeCommand.MESSAGE)
public class HelloCommand {

    public static final String NAME = "/start";

    @CommandFirst
    public void greeting(TelegramLongPollingEngine engine,
                         @ParamName("chatId") Long chatId,
                         CommandContext context){
             MessageOperations.sendNewMessage(
                     chatId,
                     "Привет, " + context.getUpdate().getMessage().getFrom().getFirstName() + "!\n"
                             + "Жми меню, там вся инфа."
                     ,
                     engine
             );
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/stop_bot", "Приостановить работу бота"));
        listOfCommands.add(new BotCommand("/start_bot", "Возобновить работу бота"));
        listOfCommands.add(new BotCommand("/ping_bot", "Статус бота"));

        context.getEngine().executeNotException(
                new SetMyCommands(listOfCommands,
                        new BotCommandScopeDefault(),
                        null)
        );

    }
}
