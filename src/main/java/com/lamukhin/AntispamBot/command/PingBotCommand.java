package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.util.MessageOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;
import ru.wdeath.managerbot.lib.bot.annotations.CommandFirst;
import ru.wdeath.managerbot.lib.bot.annotations.CommandNames;
import ru.wdeath.managerbot.lib.bot.annotations.ParamName;
import ru.wdeath.managerbot.lib.bot.command.TypeCommand;

import static com.lamukhin.AntispamBot.util.ResponseMessage.HELLO;

@Component
@CommandNames(value = PingBotCommand.NAME, type = TypeCommand.MESSAGE)
public class PingBotCommand {

    public static final String NAME = "/ping_bot";
    private final Logger log = LoggerFactory.getLogger(PingBotCommand.class);
    private long helloTime = 0l;

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
