package com.lamukhin.AntispamBot.service.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.wdeath.telegram.bot.starter.TelegramLongPollingEngine;

public interface UpdateProcessingService {

    void processGroupChatUpdate(TelegramLongPollingEngine engine, Update update);
}
