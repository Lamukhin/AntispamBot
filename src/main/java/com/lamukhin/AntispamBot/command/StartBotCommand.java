package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.listener.CustomUpdateListener;
import com.lamukhin.AntispamBot.service.interfaces.AdminService;
import com.lamukhin.AntispamBot.util.CommandOperations;
import com.lamukhin.AntispamBot.util.MessageOperations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;
import ru.wdeath.managerbot.lib.bot.annotations.CommandFirst;
import ru.wdeath.managerbot.lib.bot.annotations.CommandNames;
import ru.wdeath.managerbot.lib.bot.annotations.ParamName;
import ru.wdeath.managerbot.lib.bot.command.TypeCommand;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@CommandNames(value = StartBotCommand.NAME, type = TypeCommand.MESSAGE)
public class StartBotCommand {

    public static final String NAME = "/start_bot";
    private final AdminService adminService;
    private final CustomUpdateListener customUpdateListener;
    @Value("${bot_owner_tg_id}")
    private long botOwnerId;

    @CommandFirst
    public void startBot(TelegramLongPollingEngine engine,
                         Update update,
                         @ParamName("chatId") Long chatId,
                         @ParamName("userId") Long userId) {

        if (adminService.hasAdminStatusByUserId(userId)||(userId.equals(botOwnerId))) {
            if (customUpdateListener.getSwitcher().isPaused()) {
                String switcherName = CommandOperations.invokeFullNameFromUser(update.getMessage().getFrom());
                customUpdateListener.getSwitcher().setPaused(false);
                customUpdateListener.getSwitcher().setLastSwitcherName(switcherName);
                customUpdateListener.getSwitcher().setLastSwitchTimestamp(System.currentTimeMillis()+ 3 * 60 * 60 * 1000);
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

    public StartBotCommand(AdminService adminService, CustomUpdateListener customUpdateListener) {
        this.adminService = adminService;
        this.customUpdateListener = customUpdateListener;
    }
}
