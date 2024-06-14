package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.role.Admins;
import com.lamukhin.AntispamBot.service.interfaces.MetadataService;
import com.lamukhin.AntispamBot.service.interfaces.TextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatMember;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;
import ru.wdeath.managerbot.lib.bot.annotations.CommandFirst;
import ru.wdeath.managerbot.lib.bot.annotations.CommandNames;
import ru.wdeath.managerbot.lib.bot.annotations.ParamName;
import ru.wdeath.managerbot.lib.bot.command.TypeCommand;

@Component
@CommandNames(value = BanCommand.NAME, type = TypeCommand.MESSAGE)
public class BanCommand {

    public static final String NAME = "!spam";
    private final TextService textService;
    private final MetadataService metadataService;
    private final Admins admins;

    private final Logger log = LoggerFactory.getLogger(BanCommand.class);

    @CommandFirst
    public void banManually(TelegramLongPollingEngine engine,
                            @ParamName("userId") Long userId,
                            @ParamName("chatId") Long chatId,
                            Update update) {
        if ((admins.getSet().contains(String.valueOf(userId)))
                && (!(userId.equals(chatId)))) {
            long spammerId = update.getMessage().getReplyToMessage().getFrom().getId();
            if (!(admins.getSet().contains(String.valueOf(spammerId)))) {
                String spamMessage = update.getMessage().getReplyToMessage().getText();
                String[] wordsOfMessage = textService.invokeWordsFromRawMessage(spamMessage, null);
                textService.saveMessageIntoDictionary(wordsOfMessage);
                var send = BanChatMember.builder()
                        .chatId(chatId)
                        .userId(spammerId)
                        .untilDate(0)
                        .revokeMessages(true)
                        .build();
                engine.executeNotException(send);
                metadataService.updateDeletedMessages(engine.getBotUsername());
                metadataService.updateBannedUsers(engine.getBotUsername());
                log.warn("Banned a spammer with ID {} manually", spammerId);
            }

        }
    }

    public BanCommand(TextService textService, MetadataService metadataService, Admins admins) {
        this.textService = textService;
        this.metadataService = metadataService;
        this.admins = admins;
    }
}
