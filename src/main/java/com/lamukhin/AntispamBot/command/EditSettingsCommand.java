package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.algorithm.SearchSettings;
import com.lamukhin.AntispamBot.util.MessageOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import ru.wdeath.telegram.bot.starter.TelegramLongPollingEngine;
import ru.wdeath.telegram.bot.starter.annotations.CommandFirst;
import ru.wdeath.telegram.bot.starter.annotations.CommandNames;
import ru.wdeath.telegram.bot.starter.annotations.CommandOther;
import ru.wdeath.telegram.bot.starter.annotations.ParamName;
import ru.wdeath.telegram.bot.starter.command.CommandContext;
import ru.wdeath.telegram.bot.starter.command.TypeCommand;
import ru.wdeath.telegram.bot.starter.session.UserBotSession;

import static com.lamukhin.AntispamBot.util.ResponseMessage.*;

@Component
@CommandNames(value = EditSettingsCommand.NAME, type = TypeCommand.CALLBACK)
public class EditSettingsCommand {

    public static final String NAME = "edit-settings";
    private final SearchSettings searchSettings;

    private final Logger log = LoggerFactory.getLogger(EditSettingsCommand.class);
    @Value("${bot_owner_tg_id}")
    private long botOwnerId;

    @CommandFirst
    public void showSettings(TelegramLongPollingEngine engine,
                             @ParamName("chatId") Long chatId,
                             @ParamName("userId") Long userId,
                             CommandContext context,
                             UserBotSession userBotSession) {

        if (userId.equals(botOwnerId)) {
            String data = (String) context.getData();
            switch (data) {
                case "short" -> {
                    MessageOperations.sendNewMessage(
                            chatId,
                            String.format(SEND_LENGTH_VALUES, "коротких"),
                            ParseMode.MARKDOWN,
                            engine
                    );
                    userBotSession.setData("short");
                }
                case "middle" -> {
                    MessageOperations.sendNewMessage(
                            chatId,
                            String.format(SEND_LENGTH_VALUES, "средних"),
                            ParseMode.MARKDOWN,
                            engine
                    );
                    userBotSession.setData("middle");
                }
                case "long" -> {
                    MessageOperations.sendNewMessage(
                            chatId,
                            String.format(SEND_LENGTH_VALUES, "длинных"),
                            ParseMode.MARKDOWN,
                            engine
                    );
                    userBotSession.setData("long");
                }
                case "word" -> {
                    MessageOperations.sendNewMessage(
                            chatId,
                            SEND_WORD_COEF,
                            ParseMode.MARKDOWN,
                            engine
                    );
                    userBotSession.setData("word");
                }
                case "lower" -> {
                    MessageOperations.sendNewMessage(
                            chatId,
                            SEND_LOWER_LIMIT_COEF,
                            ParseMode.MARKDOWN,
                            engine
                    );
                    userBotSession.setData("lower");
                }
            }
        }
    }

    @CommandOther
    public void changeData(
            TelegramLongPollingEngine engine,
            @ParamName("chatId") Long chatId,
            UserBotSession userBotSession,
            CommandContext context) {

        String newMessage;
        String[] values;
        try {
            newMessage = context.getUpdate().getMessage().getText();
            values = newMessage.split(" ");
        } catch (Exception ex) {
            MessageOperations.sendNewMessage(
                    chatId,
                    ERROR_PROCESSING_MESSAGE,
                    null,
                    engine
            );
            return;
        }

        switch ((String) userBotSession.getData()) {
            case "short" -> {
                searchSettings.getSegmentForShort().setStart(Integer.parseInt(values[0]));
                searchSettings.getSegmentForShort().setEnd(Integer.parseInt(values[1]));
                searchSettings.setCoefForShortMessage(Double.parseDouble(values[2]));
                MessageOperations.sendNewMessage(
                        chatId,
                        String.format(SAVED_LENGTH_VALUES, "коротких"),
                        ParseMode.MARKDOWN,
                        engine
                );
            }
            case "middle" -> {
                searchSettings.getSegmentForMiddle().setStart(Integer.parseInt(values[0]));
                searchSettings.getSegmentForMiddle().setEnd(Integer.parseInt(values[1]));
                searchSettings.setCoefForMiddleMessage(Double.parseDouble(values[2]));
                MessageOperations.sendNewMessage(
                        chatId,
                        String.format(SAVED_LENGTH_VALUES, "средних"),
                        ParseMode.MARKDOWN,
                        engine
                );
            }
            case "long" -> {
                searchSettings.getSegmentForLong().setStart(Integer.parseInt(values[0]));
                searchSettings.getSegmentForLong().setEnd(Integer.parseInt(values[1]));
                searchSettings.setCoefForLongMessage(Double.parseDouble(values[2]));
                MessageOperations.sendNewMessage(
                        chatId,
                        String.format(SAVED_LENGTH_VALUES, "длинных"),
                        ParseMode.MARKDOWN,
                        engine
                );
            }
            case "word" -> {
                searchSettings.setCoefForCurrentWord(Double.parseDouble(values[0]));
                MessageOperations.sendNewMessage(
                        chatId,
                        SAVED_WORD_COEF,
                        ParseMode.MARKDOWN,
                        engine
                );
            }
            case "lower" -> {
                searchSettings.setCoefForLowerLimit(Double.parseDouble(values[0]));
                MessageOperations.sendNewMessage(
                        chatId,
                        SAVED_LOWER_LIMIT_COEF,
                        ParseMode.MARKDOWN,
                        engine
                );
            }
        }
        userBotSession.stop();
    }

    public EditSettingsCommand(SearchSettings searchSettings) {
        this.searchSettings = searchSettings;
    }
}
