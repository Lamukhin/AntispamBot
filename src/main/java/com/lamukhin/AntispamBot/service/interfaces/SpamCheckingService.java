package com.lamukhin.AntispamBot.service.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;

public interface SpamCheckingService {

    void checkUpdate(Update update, TelegramLongPollingEngine engine);
}
