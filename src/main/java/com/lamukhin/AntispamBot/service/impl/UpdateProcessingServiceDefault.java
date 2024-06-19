package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.service.interfaces.AdminService;
import com.lamukhin.AntispamBot.service.interfaces.MessageCountService;
import com.lamukhin.AntispamBot.service.interfaces.SpamCheckingService;
import com.lamukhin.AntispamBot.service.interfaces.UpdateProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;

/*
    Root service of the application. "processGroupChatUpdate()" method processes
    an every income message from a group chat. Private messages are processed by
    AntispamBot/libs/wds-program-agent-telegram-bot-lib
 */

@Service
public class UpdateProcessingServiceDefault implements UpdateProcessingService {

    private final Logger log = LoggerFactory.getLogger(UpdateProcessingServiceDefault.class);
    private final MessageCountService messageCountService;
    private final SpamCheckingService spamCheckingService;
    private final AdminService adminService;
    @Value("${bot_owner_tg_id}")
    private long botOwnerId;

    @Override
    public void processGroupChatUpdate(TelegramLongPollingEngine engine, Update update) {
        //TODO: внедрить jooq. пока не вышло, так как проблема с плагином. использую jpa
        if (update.getMessage().getChat().getType().equals("supergroup")) {
            if (update.hasMessage()) {
                long userTelegramId = update.getMessage().getFrom().getId();
                Long amount = messageCountService.amountOfMessages(userTelegramId);

                //owner's and admin's requests are always processed
                if ((userTelegramId == botOwnerId) || (adminService.hasAdminStatusByUserId(userTelegramId))) {
                    spamCheckingService.checkUpdate(update, engine);
                } else //if a user is trusted, update is skipped
                    if ((amount != null) && (amount >= 5)) {
                    return;
                } else //if a user is new in TG, and we don't "meet" him again
                    if ((amount == null) && (newTelegramUser(userTelegramId))) {
                    messageCountService.saveNewMember(userTelegramId);
                    spamCheckingService.checkUpdate(update, engine);
                } else //if a user is new in TG, but we don't trust him yet.
                    if (newTelegramUser(userTelegramId)) {
                    messageCountService.updateAmount(userTelegramId);
                    spamCheckingService.checkUpdate(update, engine);
                }
            }
        }

    }

    private boolean newTelegramUser(long userTelegramId) {
        /*
            Usually spammers are new TG accounts.
         */
        long MinId19062024 = 6800000000L;
        return userTelegramId > MinId19062024;
    }

    public UpdateProcessingServiceDefault(MessageCountService messageCountService,
                                          SpamCheckingService spamCheckingService, AdminService adminService) {
        this.messageCountService = messageCountService;
        this.spamCheckingService = spamCheckingService;
        this.adminService = adminService;
    }
}
