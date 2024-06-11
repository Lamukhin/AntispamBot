package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.role.Admins;
import com.lamukhin.AntispamBot.util.MessageOperations;
import org.springframework.beans.factory.annotation.Value;
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

import static com.lamukhin.AntispamBot.util.ResponseMessage.HELLO;

@Component
@CommandNames(value = HelloCommand.NAME, type = TypeCommand.MESSAGE)
public class HelloCommand {

    public static final String NAME = "/start";
    private final Admins admins;
    @Value("${bot_owner_tg_id}")
    private long botOwnerId;

    @CommandFirst
    public void greeting(TelegramLongPollingEngine engine,
                         @ParamName("userId") Long userId,
                         @ParamName("chatId") Long chatId,
                         CommandContext context) {
        context.getEngine().executeNotException(
                new SetMyCommands(
                        listOfCommands(userId),
                        new BotCommandScopeDefault(),
                        null
                )
        );
        String userFirstName = context.getUpdate().getMessage().getFrom().getFirstName();
        MessageOperations.sendNewMessage(
                chatId,
                String.format(HELLO, userFirstName),
                null,
                engine
        );

    }

    private List<BotCommand> listOfCommands(Long userId) {
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand(PingBotCommand.NAME, "Статус бота"));
        if (admins.getSet().contains(String.valueOf(userId))) {
            listOfCommands.add(new BotCommand(StartBotCommand.NAME, "Приостановить работу бота"));
            listOfCommands.add(new BotCommand(StopBotCommand.NAME, "Возобновить работу бота"));
            if (userId.equals(botOwnerId)) {
                listOfCommands.add(new BotCommand(AddAdminCommand.NAME, "Добавить админа"));
                listOfCommands.add(new BotCommand(SaveNewBanwordsCommand.NAME, "Пополнить словарь"));
                listOfCommands.add(new BotCommand(SearchSettingsCommand.NAME, "Настройки поиска"));
            }
        }
        return listOfCommands;
    }

    public HelloCommand(Admins admins) {
        this.admins = admins;
    }
}
