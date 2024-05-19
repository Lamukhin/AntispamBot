package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.db.entity.MessageCountEntity;
import com.lamukhin.AntispamBot.db.repo.MessageCountRepo;
import com.lamukhin.AntispamBot.service.interfaces.UpdateProcessingService;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;

import java.util.ArrayList;
import java.util.List;


@Service
public class UpdateProcessingServiceDefault implements UpdateProcessingService {

    private final Logger log = LoggerFactory.getLogger(UpdateProcessingServiceDefault.class);
    private final MessageCountRepo messageCountRepo;


    @Override
    public void processUpdate(TelegramLongPollingEngine engine, Update update) {
        //log.warn("Some sex with {}", update.getMessage().getFrom().getId());
        //TODO: внедрить jooq. пока не вышло, так как проблема с плагином. использую jpa
        long userTelegramId = update.getMessage().getFrom().getId();
        MessageCountEntity currenUser = messageCountRepo.findByIdChatTelegram(userTelegramId);

        if (currenUser.getCounter() > 10){
            return;
        } else {
            //spamCheck(update);
        }

    }

    public UpdateProcessingServiceDefault(MessageCountRepo messageCountRepo) {
        this.messageCountRepo = messageCountRepo;
    }
}
