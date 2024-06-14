package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.service.interfaces.AdminService;
import com.lamukhin.AntispamBot.util.MessageOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;
import ru.wdeath.managerbot.lib.bot.annotations.CommandFirst;
import ru.wdeath.managerbot.lib.bot.annotations.CommandNames;
import ru.wdeath.managerbot.lib.bot.annotations.CommandOther;
import ru.wdeath.managerbot.lib.bot.annotations.ParamName;
import ru.wdeath.managerbot.lib.bot.command.CommandContext;
import ru.wdeath.managerbot.lib.bot.command.TypeCommand;

import static com.lamukhin.AntispamBot.util.ResponseMessage.SAVED_NEW_ADMIN;
import static com.lamukhin.AntispamBot.util.ResponseMessage.SEND_NEW_CANDIDATE;

@Component
@CommandNames(value = AddAdminCommand.NAME, type = TypeCommand.MESSAGE)
public class AddAdminCommand {

    public static final String NAME = "/add_admin";
    private final AdminService adminService;
    private final Logger log = LoggerFactory.getLogger(SaveNewBanwordsCommand.class);

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
        if (forwardedUser != null){
            long invokedCandidateId = forwardedUser.getId();
            adminService.getSet().add(String.valueOf(invokedCandidateId));
            MessageOperations.sendNewMessage(
                    chatId,
                    String.format(SAVED_NEW_ADMIN, invokedCandidateId),
                    null,
                    engine
            );
        } else {
            String receivedCandidateId = context.getUpdate().getMessage().getText();
            adminService.getSet().add(receivedCandidateId);
            MessageOperations.sendNewMessage(
                    chatId,
                    String.format(SAVED_NEW_ADMIN, receivedCandidateId),
                    null,
                    engine
            );
        }
    }

    public AddAdminCommand(AdminService adminService) {
        this.adminService = adminService;
    }
}
