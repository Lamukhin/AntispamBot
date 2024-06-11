package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.db.entity.MetadataEntity;
import com.lamukhin.AntispamBot.listener.CustomUpdateListener;
import com.lamukhin.AntispamBot.role.Admins;
import com.lamukhin.AntispamBot.service.interfaces.MetadataService;
import com.lamukhin.AntispamBot.util.MessageOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;
import ru.wdeath.managerbot.lib.bot.annotations.CommandFirst;
import ru.wdeath.managerbot.lib.bot.annotations.CommandNames;
import ru.wdeath.managerbot.lib.bot.annotations.ParamName;
import ru.wdeath.managerbot.lib.bot.command.TypeCommand;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

import static com.lamukhin.AntispamBot.util.ResponseMessage.BOT_FULL_INFO;

@Component
@CommandNames(value = PingBotCommand.NAME, type = TypeCommand.MESSAGE)
public class PingBotCommand {

    public static final String NAME = "/ping_bot";
    private long lastHelloTime = 0L;
    private final MetadataService metadataService;
    private final CustomUpdateListener customUpdateListener;
    private final Admins admins;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final SimpleDateFormat timestampFormatter = new SimpleDateFormat("dd.MM.yyyy в HH:mm");
    private final Logger log = LoggerFactory.getLogger(PingBotCommand.class);

    @CommandFirst
    public void greeting(TelegramLongPollingEngine engine,
                         @ParamName("chatId") Long chatId,
                         @ParamName("userId") Long userId) {

        MetadataEntity metadataEntity = getThisBotMetadata(engine.getBotUsername());

        String botStatus = customUpdateListener.getSwitcher().isPaused() ? "НА ПАУЗЕ" : "РАБОТАЮ";
        String statusWord = customUpdateListener.getSwitcher().isPaused() ? "выключил" : "включил";
        String lastSwitcherName = customUpdateListener.getSwitcher().getLastSwitcherName();
        String lastSwitchTimestamp = timestampFormatter.format(customUpdateListener.getSwitcher().getLastSwitchTimestamp());
        String response = String.format(
                BOT_FULL_INFO,
                botStatus, statusWord, lastSwitcherName, lastSwitchTimestamp,
                metadataEntity.getMessagesDeleted(),
                metadataEntity.getUsersBanned(),
                metadataEntity.getDateStart().format(dateFormatter)
                );

        /*
            Стараюсь придерживаться правила: в каждом публичном методе в "командах" оставлять
            отправку сообщения, чтобы легко было понять, что получили и что тут же вернули.
            */
        //an admin can call this command at any time
        if (admins.getSet().contains(String.valueOf(userId))) {
            MessageOperations.sendNewMessage(
                    chatId,
                    response,
                    ParseMode.MARKDOWN,
                    engine);
        } else
        //but a default user don't have to flood
            if (System.currentTimeMillis() - lastHelloTime >= 600000) { //once in a 10 mins
            MessageOperations.sendNewMessage(
                    chatId,
                    response,
                    ParseMode.MARKDOWN,
                    engine);
            lastHelloTime = System.currentTimeMillis();
        }
    }

    private MetadataEntity getThisBotMetadata(String botUsername) {
        MetadataEntity metadataEntity = metadataService.findMetadata(botUsername);
        if (metadataEntity == null) {
            metadataEntity = metadataService.saveMetadata(
                    new MetadataEntity(botUsername)
            );
            log.warn("Saved new metadata for {}", botUsername);
        }
        return metadataEntity;
    }

    public PingBotCommand(MetadataService metadataService, CustomUpdateListener customUpdateListener, Admins admins) {
        this.metadataService = metadataService;
        this.customUpdateListener = customUpdateListener;
        this.admins = admins;
    }
}
