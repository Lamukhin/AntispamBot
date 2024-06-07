package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.service.impl.SpamCheckingServiceDefault;
import com.lamukhin.AntispamBot.service.interfaces.SpamCheckingService;
import com.lamukhin.AntispamBot.util.MessageOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;
import ru.wdeath.managerbot.lib.bot.annotations.CommandFirst;
import ru.wdeath.managerbot.lib.bot.annotations.CommandNames;
import ru.wdeath.managerbot.lib.bot.annotations.CommandOther;
import ru.wdeath.managerbot.lib.bot.annotations.ParamName;
import ru.wdeath.managerbot.lib.bot.command.CommandContext;
import ru.wdeath.managerbot.lib.bot.command.TypeCommand;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
@CommandNames(value = SaveNewBanwordsCallback.NAME, type = TypeCommand.MESSAGE)
public class SaveNewBanwordsCallback {

    public static final String NAME = "/new_banwords";
    private final Logger log = LoggerFactory.getLogger(SaveNewBanwordsCallback.class);
    private final SpamCheckingServiceDefault spamCheckingServiceDefault;

    public SaveNewBanwordsCallback(SpamCheckingServiceDefault spamCheckingServiceDefault) {
        this.spamCheckingServiceDefault = spamCheckingServiceDefault;
    }


    @CommandFirst
    public void prepareToSave(TelegramLongPollingEngine engine,
                      @ParamName("chatId") Long chatId){
        MessageOperations.sendNewMessage(
                chatId,
                "Отправьте сообщение, которое сохранится в словарь",
                engine
        );
    }

    @CommandOther
    public void saveNewBanwords(TelegramLongPollingEngine engine,
                                @ParamName("chatId") Long chatId,
                                CommandContext context){
        log.warn("Пришла месага в команд озер для сейва {}", context.getUpdate().getMessage().getText());
        //String data = (String) context.getData();
        //String[] words = spamCheckingServiceDefault.invokeWordsFromRawMessage(data);
        //spamCheckingServiceDefault.saveMessageIntoDictionary(words);
        log.warn("сохранили новые банворды");
    }
}
