package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.role.Admins;
import com.lamukhin.AntispamBot.util.MessageOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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
        if (chatId.equals(botOwnerId)) {
            MessageOperations.sendNewMessage(
                    chatId,
                    "Отправьте сообщение от кандидата в админы",
                    null,
                    engine
            );
        }
    }

    @CommandOther
    public void registerNewAdmin(TelegramLongPollingEngine engine,
                                 @ParamName("chatId") Long chatId,
                                 CommandContext context) {
        try {
            long candidateId = context.getUpdate().getMessage().getForwardFrom().getId();
            admins.getSet().add(String.valueOf(candidateId));
            MessageOperations.sendNewMessage(
                    chatId,
                    "Получили TG ID " + candidateId + " юзера и сохранили нового админа.",
                    null,
                    engine
            );
        } catch (Exception ex) {
            MessageOperations.sendNewMessage(
                    chatId,
                    "Не удалось извлечь ID. Вероятно, он закрыт настройками приватности. ",
                    null,
                    engine
            );
        }
    }

    public AddAdminCommand(Admins admins) {
        this.admins = admins;
    }
}
