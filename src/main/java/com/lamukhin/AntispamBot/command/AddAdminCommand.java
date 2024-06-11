package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.role.Admins;
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

@Component
@CommandNames(value = AddAdminCommand.NAME, type = TypeCommand.MESSAGE)
public class AddAdminCommand {

    public static final String NAME = "/add_admin";
    private final Admins admins;
    private final Logger log = LoggerFactory.getLogger(SaveNewBanwordsCommand.class);

    @Value("${bot_owner_tg_id}")
    private long botOwnerId;

    @CommandFirst
    public void getCandidate(TelegramLongPollingEngine engine,
                             @ParamName("chatId") Long chatId) {
        /*
            Эта проверка сразу проверяет, личка ли это и пишет ли создатель.
            Поскольку id личной переписки с ботом равен юзер айди в Телеграм Апи.
         */
        if (chatId.equals(botOwnerId)) {
            MessageOperations.sendNewMessage(
                    chatId,
                    "Отправьте сообщение от кандидата в админы или его userId",
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
            long candidateId = forwardedUser.getId();
            admins.getSet().add(String.valueOf(candidateId));
            MessageOperations.sendNewMessage(
                    chatId,
                    "Получили TG ID " + candidateId + " юзера и сохранили нового админа.",
                    null,
                    engine
            );
        } else {
            String receivedCandidateId = context.getUpdate().getMessage().getText();
            admins.getSet().add(receivedCandidateId);
            MessageOperations.sendNewMessage(
                    chatId,
                    "Получили TG ID " + receivedCandidateId + " юзера и сохранили нового админа.",
                    null,
                    engine
            );
        }
    }

    public AddAdminCommand(Admins admins) {
        this.admins = admins;
    }
}
