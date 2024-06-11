package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.listener.CustomUpdateListener;
import com.lamukhin.AntispamBot.role.Admins;
import com.lamukhin.AntispamBot.util.MessageOperations;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
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
                         Update update,
                         @ParamName("chatId") Long chatId,
                         @ParamName("userId") Long userId) {

        if (admins.getSet().contains(String.valueOf(userId))) {
            if (customUpdateListener.getSwitcher().isPaused()) {
                String switcherName = invokeFullNameFromUpdate(update);
                customUpdateListener.getSwitcher().setPaused(false);
                customUpdateListener.getSwitcher().setLastSwitcherName(switcherName);
                customUpdateListener.getSwitcher().setLastSwitchTimestamp(System.currentTimeMillis());
                MessageOperations.sendNewMessage(
                        chatId,
                        "Бот запущен",
                        null,
                        engine
                );
            } else {
                MessageOperations.sendNewMessage(
                        chatId,
                        "Бот итак работает. Подробнее /ping_bot",
                        null,
                        engine
                );
            }
        }

    }

    // TODO: если обслуживающих методов наберется несколько,
    //  вынести в отдельный сервис
    private String invokeFullNameFromUpdate(Update update) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(update.getMessage().getFrom().getFirstName());
        String lastName = update.getMessage().getFrom().getLastName();
        if (lastName != null) {
            stringBuilder.append(" ");
            stringBuilder.append(lastName);
        }
        return stringBuilder.toString();
    }

    public StartBotCommand(Admins admins, CustomUpdateListener customUpdateListener) {
        this.admins = admins;
        this.customUpdateListener = customUpdateListener;
    }
}
