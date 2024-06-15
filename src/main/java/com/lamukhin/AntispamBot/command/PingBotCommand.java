package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.db.entity.MetadataEntity;
import com.lamukhin.AntispamBot.db.repo.DictionaryRepo;
import com.lamukhin.AntispamBot.db.repo.MessageCountRepo;
import com.lamukhin.AntispamBot.listener.CustomUpdateListener;
import com.lamukhin.AntispamBot.service.interfaces.AdminService;
import com.lamukhin.AntispamBot.service.interfaces.MetadataService;
import com.lamukhin.AntispamBot.util.MessageOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
    private final MessageCountRepo messageCountRepo;
    private long lastHelloTime = 0L;
    private final MetadataService metadataService;
    private final CustomUpdateListener customUpdateListener;
    private final AdminService adminService;
    private final DictionaryRepo dictionaryRepo;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final SimpleDateFormat timestampFormatter = new SimpleDateFormat("dd.MM.yyyy в HH:mm");
    private final Logger log = LoggerFactory.getLogger(PingBotCommand.class);
    @Value("${bot_owner_tg_id}")
    private long botOwnerId;

    @CommandFirst
    public void greeting(TelegramLongPollingEngine engine,
                         @ParamName("chatId") Long chatId,
                         @ParamName("userId") Long userId) {

        if(!(chatId.equals(userId))) {
            MetadataEntity metadataEntity = getThisBotMetadata(engine.getBotUsername());

            String botStatus = customUpdateListener.getSwitcher().isPaused() ? "НА ПАУЗЕ" : "РАБОТАЮ";
            String statusWord = customUpdateListener.getSwitcher().isPaused() ? "выключил" : "включил";
            String lastSwitcherName = customUpdateListener.getSwitcher().getLastSwitcherName();
            String lastSwitchTimestamp = timestampFormatter.format(customUpdateListener.getSwitcher().getLastSwitchTimestamp());
            //не надо вдумываться, что тут происходит. это - визуальный сахар в чате. на бизнес логику не влияет.
            String response = String.format(
                    BOT_FULL_INFO,
                    botStatus, statusWord, lastSwitcherName, lastSwitchTimestamp,
                    metadataEntity.getMessagesDeleted(),
                    metadataEntity.getUsersBanned(),
                    dictionaryRepo.count(),
                    metadataEntity.getDateStart().format(dateFormatter)
            );

        /*
            Стараюсь придерживаться правила: в каждом публичном методе в "командах" оставлять
            отправку сообщения, чтобы легко было понять, что получили и что тут же вернули.
        */
            //an admin or an owner :) can call this command at any time
            if ((adminService.hasAdminStatusByUserId(userId)) || (userId.equals(botOwnerId))) {
                MessageOperations.sendNewMessage(
                        chatId,
                        response,
                        ParseMode.MARKDOWN,
                        engine);
            } else
                //but a default user don't have to flood
                if (System.currentTimeMillis() - lastHelloTime >= 600000) { //once in 10 mins
                    MessageOperations.sendNewMessage(
                            chatId,
                            response,
                            ParseMode.MARKDOWN,
                            engine);
                    lastHelloTime = System.currentTimeMillis();
                }
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

    public PingBotCommand(MetadataService metadataService, CustomUpdateListener customUpdateListener, MessageCountRepo messageCountRepo, AdminService adminService, DictionaryRepo dictionaryRepo) {
        this.metadataService = metadataService;
        this.customUpdateListener = customUpdateListener;
        this.messageCountRepo = messageCountRepo;
        this.adminService = adminService;
        this.dictionaryRepo = dictionaryRepo;
    }
}
