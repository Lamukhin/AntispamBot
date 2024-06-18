package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.listener.CustomUpdateListener;
import com.lamukhin.AntispamBot.service.interfaces.AdminService;
import com.lamukhin.AntispamBot.util.MessageOperations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;
import ru.wdeath.managerbot.lib.bot.annotations.CommandFirst;
import ru.wdeath.managerbot.lib.bot.annotations.CommandNames;
import ru.wdeath.managerbot.lib.bot.annotations.ParamName;
import ru.wdeath.managerbot.lib.bot.command.TypeCommand;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@CommandNames(value = StopBotCommand.NAME, type = TypeCommand.MESSAGE)
public class StopBotCommand {

    public static final String NAME = "/stop_bot";
    private final CustomUpdateListener customUpdateListener;
    private final AdminService adminService;
    @Value("${bot_owner_tg_id}")
    private long botOwnerId;

    @CommandFirst
    public void stopBot(TelegramLongPollingEngine engine,
                        Update update,
                        @ParamName("chatId") Long chatId,
                        @ParamName("userId") Long userId) {

        if ((adminService.hasAdminStatusByUserId(userId))||(userId.equals(botOwnerId))) {
            if (!(customUpdateListener.getSwitcher().isPaused())) {

                String switcherName = invokeFullNameFromUser(update.getMessage().getFrom());
                customUpdateListener.getSwitcher().setPaused(true);
                customUpdateListener.getSwitcher().setLastSwitcherName(switcherName);
                customUpdateListener.getSwitcher().setLastSwitchTimestamp(System.currentTimeMillis()+ 3 * 60 * 60 * 1000);
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
    private String invokeFullNameFromUser(User forwardedUser) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(forwardedUser.getFirstName());
        String lastName = forwardedUser.getLastName();
        if (lastName != null) {
            stringBuilder.append(" ");
            stringBuilder.append(lastName);
        }
        return stringBuilder.toString();
    }

    public StopBotCommand(CustomUpdateListener customUpdateListener, AdminService adminService) {
        this.adminService = adminService;
        this.customUpdateListener = customUpdateListener;
    }
}
