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
@CommandNames(value = StopBotCommand.NAME, type = TypeCommand.MESSAGE)
public class StopBotCommand {

    public static final String NAME = "/stop_bot";
    private final Admins admins;
    private final CustomUpdateListener customUpdateListener;

    @CommandFirst
    public void stopBot(TelegramLongPollingEngine engine,
                        Update update,
                        @ParamName("chatId") Long chatId,
                        @ParamName("userId") Long userId) {

        if (admins.getSet().contains(String.valueOf(userId))) {
            if (!(customUpdateListener.getSwitcher().isPaused())) {

                String switcherName = invokeFullNameFromUpdate(update);
                customUpdateListener.getSwitcher().setPaused(true);
                customUpdateListener.getSwitcher().setLastSwitcherName(switcherName);
                customUpdateListener.getSwitcher().setLastSwitchTimestamp(System.currentTimeMillis());
                MessageOperations.sendNewMessage(
                        chatId,
                        "Бот приостановлен.",
                        null,
                        engine
                );
            } else {
                MessageOperations.sendNewMessage(
                        chatId,
                        "Бот уже на паузе. Подробнее /ping_bot",
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

    public StopBotCommand(Admins admins, CustomUpdateListener customUpdateListener) {
        this.admins = admins;
        this.customUpdateListener = customUpdateListener;
    }
}
