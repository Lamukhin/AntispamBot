package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.service.interfaces.TextService;
import com.lamukhin.AntispamBot.util.MessageOperations;
import com.lamukhin.AntispamBot.util.TextFiltrationProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.wdeath.telegram.bot.starter.TelegramLongPollingEngine;
import ru.wdeath.telegram.bot.starter.annotations.CommandFirst;
import ru.wdeath.telegram.bot.starter.annotations.CommandNames;
import ru.wdeath.telegram.bot.starter.annotations.CommandOther;
import ru.wdeath.telegram.bot.starter.annotations.ParamName;
import ru.wdeath.telegram.bot.starter.command.CommandContext;
import ru.wdeath.telegram.bot.starter.command.TypeCommand;

import java.util.Arrays;

import static com.lamukhin.AntispamBot.util.ResponseMessage.*;

@Component
@CommandNames(value = SaveNewBanwordsCommand.NAME, type = TypeCommand.MESSAGE)
public class SaveNewBanwordsCommand {

    public static final String NAME = "/new_banwords";
    private final TextService textService;
    private final Logger log = LoggerFactory.getLogger(SaveNewBanwordsCommand.class);
    @Value("${bot_owner_tg_id}")
    private long botOwnerId;

    @CommandFirst
    public void prepareToSave(TelegramLongPollingEngine engine,
                              @ParamName("chatId") Long chatId) {

        if (chatId.equals(botOwnerId)) {
            MessageOperations.sendNewMessage(
                    chatId,
                    SEND_WORDS_TO_SAVE,
                    null,
                    engine
            );
        }
    }

    @CommandOther
    public void saveNewBanwords(TelegramLongPollingEngine engine,
                                @ParamName("chatId") Long chatId,
                                CommandContext context) {
        String newMessage;
        String[] words;
        try {
            newMessage = context.getUpdate().getMessage().getText();
            words = textService.invokeWordsFromRawMessage(newMessage, TextFiltrationProps.DEFAULT);
        } catch (Exception ex) {
            MessageOperations.sendNewMessage(
                    chatId,
                    ERROR_PROCESSING_MESSAGE,
                    null,
                    engine
            );
            return;
        }

        textService.saveMessageIntoDictionary(words);
        String newBanwordsSavedResponse = String.format(
                NEW_WORDS_SAVED,
                toBeautifulString(words),
                textService.getCachedDictionary().size()
        );
        MessageOperations.sendNewMessage(
                chatId,
                newBanwordsSavedResponse,
                null,
                engine
        );
        log.warn("New banwords has been added into the dictionary.");
    }

    private String toBeautifulString(String[] words) {
        StringBuilder stringBuilder = new StringBuilder();
        Arrays.asList(words).forEach(word -> {
            stringBuilder.append(word);
            stringBuilder.append(", ");
        });
        return stringBuilder.toString();
    }

    public SaveNewBanwordsCommand(TextService textService) {
        this.textService = textService;
    }
}
