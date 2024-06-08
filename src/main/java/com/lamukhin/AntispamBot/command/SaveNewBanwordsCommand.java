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

import java.util.Arrays;

import static com.lamukhin.AntispamBot.util.ResponseMessage.NEW_WORDS_SAVED;
import static com.lamukhin.AntispamBot.util.ResponseMessage.SEND_WORDS_TO_SAVE;

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
        if(chatId == 260113861L) {
            MessageOperations.sendNewMessage(
                    chatId,
                    SEND_WORDS_TO_SAVE,
                    engine
            );
        }
    }

    @CommandOther
    public void saveNewBanwords(TelegramLongPollingEngine engine,
                                @ParamName("chatId") Long chatId,
                                CommandContext context){
        if(chatId == 260113861L) {
            String newMessage = context.getUpdate().getMessage().getText();
            String[] words = textService.invokeWordsFromRawMessage(newMessage);
            textService.saveMessageIntoDictionary(words);
            log.warn("New banwords has been added into the dictionary.");
            String newBanwordsSavedResponse = String.format(
                    NEW_WORDS_SAVED,
                    toBeautifulString(words),
                    textService.getCachedDictionary().size()
            );
            MessageOperations.sendNewMessage(
                    chatId,
                    newBanwordsSavedResponse,
                    engine
            );
        }
    }

    private String toBeautifulString(String[] words) {
        StringBuilder stringBuilder = new StringBuilder();
        Arrays.asList(words).forEach(word -> {
            stringBuilder.append(word);
            stringBuilder.append(", ");
        });
        return stringBuilder.toString();
    }
}
