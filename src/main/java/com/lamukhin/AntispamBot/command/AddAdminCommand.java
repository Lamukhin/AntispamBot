package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.role.Admins;
import com.lamukhin.AntispamBot.util.MessageOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger log = LoggerFactory.getLogger(AddAdminCommand.class);


    @CommandFirst
    public void addNewAdmin(TelegramLongPollingEngine engine,
                            @ParamName("chatId") Long chatId) {
        if (chatId == 260113861L) {
            MessageOperations.sendNewMessage(
                    chatId,
                    "Отправьте сообщение от кандидата в админы",
                    engine
            );
        }

    }

    @CommandOther
    public void registerNewAdmin(TelegramLongPollingEngine engine,
                                 @ParamName("chatId") Long chatId,
                                 CommandContext context) {
        long candidateId = 0;
        try {
            candidateId = context.getUpdate().getMessage().getForwardFrom().getId();
            admins.getSet().add(String.valueOf(candidateId));
            MessageOperations.sendNewMessage(
                    chatId,
                    "Получили id " + candidateId + " юзера и сохранили как админа.",
                    engine
            );
        } catch (Exception ex) {
            MessageOperations.sendNewMessage(
                    chatId,
                    "Не удалось извлечь ID. Вероятно, он закрыт настройками приватности. ",
                    engine
            );
        }
    }

    public AddAdminCommand(Admins admins) {
        this.admins = admins;
    }
}
