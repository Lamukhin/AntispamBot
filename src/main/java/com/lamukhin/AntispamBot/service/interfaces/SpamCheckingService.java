package com.lamukhin.AntispamBot.service.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.wdeath.telegram.bot.starter.TelegramLongPollingEngine;

public interface SpamCheckingService {

    void checkUpdate(Update update, TelegramLongPollingEngine engine);
}
