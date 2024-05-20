package com.lamukhin.AntispamBot.service.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface SpamCheckingService {

    void checkUpdate(Update update);
}
