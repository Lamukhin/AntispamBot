package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.service.interfaces.AdminService;
import com.lamukhin.AntispamBot.util.CommandOperations;
import com.lamukhin.AntispamBot.util.MessageOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.wdeath.telegram.bot.starter.TelegramLongPollingEngine;
import ru.wdeath.telegram.bot.starter.annotations.CommandFirst;
import ru.wdeath.telegram.bot.starter.annotations.CommandNames;
import ru.wdeath.telegram.bot.starter.annotations.CommandOther;
import ru.wdeath.telegram.bot.starter.annotations.ParamName;
import ru.wdeath.telegram.bot.starter.command.CommandContext;
import ru.wdeath.telegram.bot.starter.command.TypeCommand;

import static com.lamukhin.AntispamBot.util.ResponseMessage.*;

@Component
@CommandNames(value = AddAdminCommand.NAME, type = TypeCommand.MESSAGE)
public class AddAdminCommand {

    public static final String NAME = "/add_admin";
    private final AdminService adminService;
    private final Logger log = LoggerFactory.getLogger(AddAdminCommand.class);

    @Value("${bot_owner_tg_id}")
    private long botOwnerId;

    @CommandFirst
    public void getCandidate(TelegramLongPollingEngine engine,
                             @ParamName("chatId") Long chatId) {
        /*
            Эта проверка сразу проверяет, личка ли это и пишет ли создатель.
            Поскольку id личной переписки с ботом равен юзер айди в Телеграм.
         */
        if (chatId.equals(botOwnerId)) {
            MessageOperations.sendNewMessage(
                    chatId,
                    SEND_NEW_CANDIDATE,
                    null,
                    engine
            );
        }
    }

    @CommandOther
    public void registerNewAdmin(TelegramLongPollingEngine engine,
                                 @ParamName("chatId") Long chatId,
                                 CommandContext context) {
        User forwardedUser = context.getUpdate().getMessage().getForwardFrom();
        long id;
        String fullName;
        try {
            if (forwardedUser != null) {
                id = forwardedUser.getId();
                fullName = CommandOperations.invokeFullNameFromUser(forwardedUser);
            } else {
                String manuallyEnteredData = context.getUpdate().getMessage().getText().trim();
                String[] idAndName = manuallyEnteredData.split(" ", 2);
                id = Long.parseLong(idAndName[0]);
                fullName = idAndName[1];
            }
            adminService.saveNewAdmin(id, fullName);
            MessageOperations.sendNewMessage(
                    chatId,
                    String.format(SAVED_NEW_ADMIN, id, fullName),
                    null,
                    engine
            );
        } catch (NumberFormatException ex) {
            MessageOperations.sendNewMessage(
                    chatId,
                    ERROR_ADDING_ADMIN,
                    null,
                    engine
            );
        }

    }

    public AddAdminCommand(AdminService adminService) {
        this.adminService = adminService;
    }
}
