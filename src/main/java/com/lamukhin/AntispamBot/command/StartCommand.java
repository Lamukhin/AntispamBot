package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.util.MessageOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;
import ru.wdeath.managerbot.lib.bot.annotations.CommandFirst;
import ru.wdeath.managerbot.lib.bot.annotations.CommandNames;
import ru.wdeath.managerbot.lib.bot.annotations.ParamName;

import static com.lamukhin.AntispamBot.util.ResponseMessage.HELLO;

@Component
@CommandNames("/ping_bot")
public class StartCommand {

    private final Logger log = LoggerFactory.getLogger(StartCommand.class);
    private long helloTime = System.currentTimeMillis();

    @CommandFirst
    public void greeting(TelegramLongPollingEngine engine,
                         @ParamName("chatId") Long chatId) {
        if (System.currentTimeMillis() - helloTime >= 1800000) {
            MessageOperations.sendNewMessage(
                    chatId,
                    HELLO,
                    engine);
            helloTime = System.currentTimeMillis();
        }
    }

}
