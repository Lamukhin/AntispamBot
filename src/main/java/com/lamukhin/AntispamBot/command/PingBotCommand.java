package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.db.entity.MetadataEntity;
import com.lamukhin.AntispamBot.listener.CustomUpdateListener;
import com.lamukhin.AntispamBot.role.Admins;
import com.lamukhin.AntispamBot.service.interfaces.MetadataService;
import com.lamukhin.AntispamBot.util.MessageOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;
import ru.wdeath.managerbot.lib.bot.annotations.CommandFirst;
import ru.wdeath.managerbot.lib.bot.annotations.CommandNames;
import ru.wdeath.managerbot.lib.bot.annotations.ParamName;
import ru.wdeath.managerbot.lib.bot.command.TypeCommand;

import java.time.format.DateTimeFormatter;

import static com.lamukhin.AntispamBot.util.ResponseMessage.BOT_STATUS;

@Component
@CommandNames(value = PingBotCommand.NAME, type = TypeCommand.MESSAGE)
public class PingBotCommand {

    public static final String NAME = "/ping_bot";
    private final Logger log = LoggerFactory.getLogger(PingBotCommand.class);
    private long lastHelloTime = 0l;
    private final MetadataService metadataService;
    private final CustomUpdateListener customUpdateListener;
    private final Admins admins;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @CommandFirst
    public void greeting(TelegramLongPollingEngine engine,
                         @ParamName("chatId") Long chatId,
                         @ParamName("userId") Long userId) {

        MetadataEntity metadataEntity = metadataService.findMetadata(engine.getBotUsername());
        if (metadataEntity == null) {
            metadataEntity = metadataService.saveMetadata(
                    new MetadataEntity(engine.getBotUsername())
            );
            log.warn("Saved new metadata for {}", engine.getBotUsername());
        }
        String botStatus = customUpdateListener.isPaused() ? "НА ПАУЗЕ" : "РАБОТАЮ";
        String response = String.format(
                BOT_STATUS,
                botStatus,
                metadataEntity.getMessagesDeleted(),
                metadataEntity.getUsersBanned(),
                metadataEntity.getDateStart().format(formatter)
                );
        //an admin can call this command at any time
        if (admins.getSet().contains(String.valueOf(userId))) {
            MessageOperations.sendNewMessage(
                    chatId,
                    response,
                    engine);
        } else
        //but a default user don't have to flood
            if (System.currentTimeMillis() - lastHelloTime >= 600000) { //once in a 10 mins
            MessageOperations.sendNewMessage(
                    chatId,
                    response,
                    engine);
            lastHelloTime = System.currentTimeMillis();
        }
    }

    public PingBotCommand(MetadataService metadataService, CustomUpdateListener customUpdateListener, Admins admins) {
        this.metadataService = metadataService;
        this.customUpdateListener = customUpdateListener;
        this.admins = admins;
    }
}
