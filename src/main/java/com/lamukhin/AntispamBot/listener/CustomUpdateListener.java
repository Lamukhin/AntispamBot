package com.lamukhin.AntispamBot.listener;

import com.lamukhin.AntispamBot.service.interfaces.UpdateProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;
import ru.wdeath.managerbot.lib.bot.interfaces.HandlerBotUpdate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/*
    This is a root component of all application.
    Every group chat message processing starts here.
 */

@Component
public class CustomUpdateListener implements HandlerBotUpdate {

    private final UpdateProcessingService updateProcessingService;

    private Switcher switcher = new Switcher(false);
    private final Logger log = LoggerFactory.getLogger(CustomUpdateListener.class);

    @Override
    public void update(TelegramLongPollingEngine engine, Update update) {
        log.warn("Processing update with a custom listener!");
        if(!switcher.isPaused()) {
            try {
                updateProcessingService.processGroupChatUpdate(engine, update);
            } catch (Exception ex) {
                log.error("Error during processing update: {}", ex.getMessage());
            }
        }
    }

    @Override
    public int priority() {
        //bigger than any system priorities (>100);
        return 201;
    }

    public class Switcher {

        private boolean paused;
        private String lastSwitcherName;
        private long lastSwitchTimestamp;

        public Switcher (boolean paused){
            this.paused = paused;
            this.lastSwitcherName = "sunshine";
            ZoneId zoneId = ZoneId.of("Europe/Moscow");
            ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
            this.lastSwitchTimestamp = zonedDateTime.toInstant().toEpochMilli();
        }

        //не уверен, что ТГ обрабатывает многопоточно запросы, но лучше перестраховаться
        public synchronized void setPaused(boolean paused) {
            this.paused = paused;
        }
        public synchronized void setLastSwitcherName(String lastSwitcherName) {
            this.lastSwitcherName = lastSwitcherName;
        }
        public synchronized void setLastSwitchTimestamp(long lastSwitchTimestamp) {
            this.lastSwitchTimestamp = lastSwitchTimestamp;
        }

        public boolean isPaused() {
            return paused;
        }
        public long getLastSwitchTimestamp() {
            return lastSwitchTimestamp;
        }
        public String getLastSwitcherName() {
            return lastSwitcherName;
        }
    }

    public CustomUpdateListener(UpdateProcessingService updateProcessingService) {
        this.updateProcessingService = updateProcessingService;
    }

    public Switcher getSwitcher() {
        return switcher;
    }


}
