package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.db.repo.MessageCountRepo;
import com.lamukhin.AntispamBot.service.interfaces.MessageCountService;
import com.lamukhin.AntispamBot.service.interfaces.SpamCheckingService;
import com.lamukhin.AntispamBot.service.interfaces.UpdateProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;


@Service
public class UpdateProcessingServiceDefault implements UpdateProcessingService {

    private final Logger log = LoggerFactory.getLogger(UpdateProcessingServiceDefault.class);
    private final MessageCountRepo messageCountRepo;
    private final MessageCountService messageCountService;
    private final SpamCheckingService spamCheckingService;


    @Override
    public void processUpdate(TelegramLongPollingEngine engine, Update update) {
        //log.warn("Some sex with {}", update.getMessage().getFrom().getId());
        //TODO: внедрить jooq. пока не вышло, так как проблема с плагином. использую jpa
        long userTelegramId = update.getMessage().getFrom().getId();
        Integer amount = messageCountService.amountOfMessages(userTelegramId);

        if ((amount != null) && (amount >= 10)){
            return;
        } else if (amount == null){
            messageCountService.saveNewMember(userTelegramId);
            spamCheckingService.checkUpdate(update);
        } else {
            spamCheckingService.checkUpdate(update);
        }

    }

    public UpdateProcessingServiceDefault(MessageCountRepo messageCountRepo,
                                          MessageCountService messageCountService, SpamCheckingService spamCheckingService) {
        this.messageCountRepo = messageCountRepo;
        this.messageCountService = messageCountService;
        this.spamCheckingService = spamCheckingService;
    }
}
