package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.service.interfaces.TextService;
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

@Component
@CommandNames(value = SaveNewBanwordsCommand.NAME, type = TypeCommand.MESSAGE)
public class SaveNewBanwordsCommand {

    public static final String NAME = "/new_banwords";
    private final Logger log = LoggerFactory.getLogger(SaveNewBanwordsCommand.class);
    private final TextService textService;

    public SaveNewBanwordsCommand(TextService textService) {
        this.textService = textService;
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
    public void saveNewBanwords(CommandContext context){
        String newMessage = context.getUpdate().getMessage().getText();
        String[] words = textService.invokeWordsFromRawMessage(newMessage);
        textService.saveMessageIntoDictionary(words);
        log.warn("New banwords has been added into the dictionary.");
    }
}
