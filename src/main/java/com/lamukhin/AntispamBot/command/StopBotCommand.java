package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.listener.CustomUpdateListener;
import com.lamukhin.AntispamBot.service.interfaces.AdminService;
import com.lamukhin.AntispamBot.util.CommandOperations;
import com.lamukhin.AntispamBot.util.MessageOperations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.wdeath.telegram.bot.starter.TelegramLongPollingEngine;
import ru.wdeath.telegram.bot.starter.annotations.CommandFirst;
import ru.wdeath.telegram.bot.starter.annotations.CommandNames;
import ru.wdeath.telegram.bot.starter.annotations.ParamName;
import ru.wdeath.telegram.bot.starter.command.TypeCommand;


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

        if(chatId.equals(userId)) {
            if ((adminService.hasAdminStatusByUserId(userId)) || (userId.equals(botOwnerId))) {
                if (!(customUpdateListener.getSwitcher().isPaused())) {

                    String switcherName = CommandOperations.invokeFullNameFromUser(update.getMessage().getFrom());
                    customUpdateListener.getSwitcher().setPaused(true);
                    customUpdateListener.getSwitcher().setLastSwitcherName(switcherName);
                    customUpdateListener.getSwitcher().setLastSwitchTimestamp(System.currentTimeMillis() + 3 * 60 * 60 * 1000);
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
    }

    public StopBotCommand(CustomUpdateListener customUpdateListener, AdminService adminService) {
        this.adminService = adminService;
        this.customUpdateListener = customUpdateListener;
    }
}
