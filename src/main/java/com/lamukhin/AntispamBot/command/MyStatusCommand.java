package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.db.entity.AdminEntity;
import com.lamukhin.AntispamBot.service.interfaces.AdminService;
import com.lamukhin.AntispamBot.util.Commands;
import com.lamukhin.AntispamBot.util.MessageOperations;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;
import ru.wdeath.managerbot.lib.bot.annotations.CommandFirst;
import ru.wdeath.managerbot.lib.bot.annotations.CommandNames;
import ru.wdeath.managerbot.lib.bot.annotations.ParamName;
import ru.wdeath.managerbot.lib.bot.command.TypeCommand;

import static com.lamukhin.AntispamBot.util.ResponseMessage.YOUR_STATUS;
/*
    An admin can check if he has rights right now to control the Bot.
 */

@Component
@CommandNames(value = MyStatusCommand.NAME, type = TypeCommand.MESSAGE)
public class MyStatusCommand {

    public static final String NAME = "/my_status";
    private final AdminService adminService;

    @CommandFirst
    public void checkStatus(TelegramLongPollingEngine engine,
                            @ParamName("userId") Long userId,
                            @ParamName("chatId") Long chatId) {

        AdminEntity admin = adminService.findByUserId(userId);
        if ((chatId.equals(userId)) && (admin != null)) {
            boolean isStillAdmin = adminService.hasAdminStatusByUserId(userId);
            String response = buildResponseByStatus(isStillAdmin);

            MessageOperations.sendNewMessage(
                    chatId,
                    response,
                    ParseMode.MARKDOWN,
                    engine
            );
        }

    }

    private String buildResponseByStatus(boolean isStillAdmin) {

        String isActiveOrNot = isStillAdmin ? "" : "не";
        String listOfAllowedCommands = Commands.getDefaultUserCommands()
                .stream()
                .map(BotCommand::getCommand)
                .toList()
                .toString();

        if (isStillAdmin) {
            listOfAllowedCommands += Commands.getAdminCommands()
                    .stream()
                    .map(BotCommand::getCommand)
                    .toList()+
                    "!spam в ответах в групповом чате";
        }

        return String.format(
                YOUR_STATUS,
                isActiveOrNot,
                listOfAllowedCommands
        );
    }

    public MyStatusCommand(AdminService adminService) {
        this.adminService = adminService;
    }
}
