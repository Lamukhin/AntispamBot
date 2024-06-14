package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.db.entity.AdminEntity;
import com.lamukhin.AntispamBot.service.interfaces.AdminService;
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

import java.util.Collections;
import java.util.List;

import static com.lamukhin.AntispamBot.util.ResponseMessage.ADMIN_REMOVED;
import static com.lamukhin.AntispamBot.util.ResponseMessage.LIST_OF_ADMINS;

@Component
@CommandNames(value = RemoveAdminCommand.NAME, type = TypeCommand.MESSAGE)
public class RemoveAdminCommand {

    public static final String NAME = "/remove_admin";
    private final AdminService adminService;
    private final Logger log = LoggerFactory.getLogger(RemoveAdminCommand.class);

    @Value("${bot_owner_tg_id}")
    private long botOwnerId;

    @CommandFirst
    public void listOfCurrentAdmins(TelegramLongPollingEngine engine,
                                    @ParamName("chatId") Long chatId) {

        if (chatId.equals(botOwnerId)) {
            List<AdminEntity> allAdmins = adminService.findAll();
            if (allAdmins.isEmpty()) {
                MessageOperations.sendNewMessage(
                        chatId,
                        "Пока не добавлено ни одного администратора группы",
                        null,
                        engine
                );
            } else {
                String listOfAdmins = buildBeautifulList(allAdmins);
                String response = String.format(
                        LIST_OF_ADMINS,
                        listOfAdmins
                );
                MessageOperations.sendNewMessage(
                        chatId,
                        response,
                        null,
                        engine
                );
            }
        }
    }

    @CommandOther
    public void removeAdmin(TelegramLongPollingEngine engine,
                            @ParamName("chatId") Long chatId,
                            CommandContext context) {
        String enteredId = context.getUpdate().getMessage().getText().trim();
        try {
            adminService.removeAdminRights(Long.parseLong(enteredId));
            String removedFullName = adminService.findByUserId(Long.parseLong(enteredId)).getFullName();
            MessageOperations.sendNewMessage(
                    chatId,
                    String.format(ADMIN_REMOVED, removedFullName),
                    null,
                    engine
            );
        } catch (Exception ex) {
            MessageOperations.sendNewMessage(
                    chatId,
                    "Что-то пошло не так, проверьте введённые данные.",
                    null,
                    engine
            );
        }
    }

    private String buildBeautifulList(List<AdminEntity> allAdmins) {
        Collections.sort(allAdmins);
        boolean oldAdminsStarted = false;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < allAdmins.size(); i++) {
            if((!allAdmins.get(i).isActive())&&(!oldAdminsStarted)){
                stringBuilder.append("_______________________________");
                stringBuilder.append("\n");
                stringBuilder.append("Бывшие админы: ");
                stringBuilder.append("\n");
                oldAdminsStarted = true;
            }
            stringBuilder.append(i);
            stringBuilder.append(") ");
            stringBuilder.append(allAdmins.get(i).getFullName());
            stringBuilder.append(" ");
            stringBuilder.append(allAdmins.get(i).getUserId());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public RemoveAdminCommand(AdminService adminService) {
        this.adminService = adminService;
    }
}
