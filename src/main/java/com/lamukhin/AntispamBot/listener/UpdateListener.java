package com.lamukhin.AntispamBot.listener;

import com.lamukhin.AntispamBot.service.interfaces.UpdateProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;
import ru.wdeath.managerbot.lib.bot.interfaces.HandlerBotUpdate;

/*
    This is a root component for all application.
    Every message processing starts here.
 */

@Component
public class UpdateListener implements HandlerBotUpdate {

    private final Logger log = LoggerFactory.getLogger(UpdateListener.class);
    private final UpdateProcessingService updateProcessingService;

    @Override
    public void update(TelegramLongPollingEngine engine, Update update) {
        try {
            updateProcessingService.processUpdate(engine, update);
        } catch (Exception ex) {
            log.error("Error during processing update: {}", ex.getMessage());
        }
    }

    @Override
    public int priority() {
        //bigger than any system priorities (>100);
        return 201;
    }

    public UpdateListener(UpdateProcessingService updateProcessingService) {
        this.updateProcessingService = updateProcessingService;
    }
}
