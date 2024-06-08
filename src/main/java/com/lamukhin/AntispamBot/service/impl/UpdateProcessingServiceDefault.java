package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.service.interfaces.MessageCountService;
import com.lamukhin.AntispamBot.service.interfaces.SpamCheckingService;
import com.lamukhin.AntispamBot.service.interfaces.UpdateProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;
/*
    Root service of the application. "processUpdate()" method processes an every income message.
 */

@Service
public class UpdateProcessingServiceDefault implements UpdateProcessingService {

    private final Logger log = LoggerFactory.getLogger(UpdateProcessingServiceDefault.class);
    private final MessageCountService messageCountService;
    private final SpamCheckingService spamCheckingService;

    /* важная пометка: тут обрабатываются сообщения только из групповых чатов.
        личные сообщения обрабатываются либой wdeath
    */
    @Override
    public void processGroupChatUpdate(TelegramLongPollingEngine engine, Update update) {
        //TODO: внедрить jooq. пока не вышло, так как проблема с плагином. использую jpa
        if (update.getMessage().getChat().getType().equals("supergroup")) {
            if (update.hasMessage()) {
                long userTelegramId = update.getMessage().getFrom().getId();
                Integer amount = messageCountService.amountOfMessages(userTelegramId);

                if ((amount != null) && (amount >= 10)) {
                    return;
                } else if (amount == null) {
                    messageCountService.saveNewMember(userTelegramId);
                    spamCheckingService.checkUpdate(update, engine);
                } else {
                    messageCountService.updateAmount(userTelegramId);
                    spamCheckingService.checkUpdate(update, engine);
                }
            }
        }

    }

    public UpdateProcessingServiceDefault(MessageCountService messageCountService,
                                          SpamCheckingService spamCheckingService) {
        this.messageCountService = messageCountService;
        this.spamCheckingService = spamCheckingService;
    }
}
