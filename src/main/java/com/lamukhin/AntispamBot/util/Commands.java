package com.lamukhin.AntispamBot.util;

import com.lamukhin.AntispamBot.command.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.ArrayList;
import java.util.List;

public final class Commands {

    private static final List<BotCommand> defaultUserCommands = new ArrayList<>() {{
        add(new BotCommand(PingBotCommand.NAME, "Статус бота"));
    }};

    private static final List<BotCommand> adminCommands = new ArrayList<>() {{
        add(new BotCommand(StopBotCommand.NAME, "Приостановить работу бота"));
        add(new BotCommand(StartBotCommand.NAME, "Возобновить работу бота"));
        add(new BotCommand(MyStatusCommand.NAME, "Ваш статус"));
    }};

    private static final List<BotCommand> ownerCommands = new ArrayList<>() {{
        add(new BotCommand(AddAdminCommand.NAME, "Добавить админа"));
        add(new BotCommand(SaveNewBanwordsCommand.NAME, "Пополнить словарь"));
        add(new BotCommand(SearchSettingsCommand.NAME, "Настройки поиска"));
    }};

    public static List<BotCommand> getDefaultUserCommands() {
        List<BotCommand> clonedList = new ArrayList<>();
        defaultUserCommands.forEach(e -> clonedList.add(e));
        return clonedList;
    }


    public static List<BotCommand> getAdminCommands() {
        List<BotCommand> clonedList = new ArrayList<>();
        adminCommands.forEach(e -> clonedList.add(e));
        return clonedList;
    }


    public static List<BotCommand> getOwnerCommands() {
        List<BotCommand> clonedList = new ArrayList<>();
        ownerCommands.forEach(e -> clonedList.add(e));
        return clonedList;
    }
}
