package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.algorithm.SearchSettings;
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
import ru.wdeath.managerbot.lib.bot.callback.CallbackData;
import ru.wdeath.managerbot.lib.bot.callback.CallbackDataSender;
import ru.wdeath.managerbot.lib.bot.command.TypeCommand;
import ru.wdeath.managerbot.lib.util.KeyboardUtil;
import java.text.DecimalFormat;
import static com.lamukhin.AntispamBot.util.ResponseMessage.CURRENT_SETTINGS;

@Component
@CommandNames(value = SearchSettingsCommand.NAME, type = TypeCommand.MESSAGE)
public class SearchSettingsCommand {
    public static final String NAME = "/settings";
    private final SearchSettings searchSettings;
    private final DecimalFormat df = new DecimalFormat("#.##");
    @Value("${bot_owner_tg_id}")
    private long botOwnerId;
    private final CallbackDataSender[][] buttons = {{
            new CallbackDataSender("Короткое", new CallbackData(EditSettingsCommand.NAME, "short")),
            new CallbackDataSender("Среднее", new CallbackData(EditSettingsCommand.NAME, "middle")),
            new CallbackDataSender("Длинное", new CallbackData(EditSettingsCommand.NAME, "long")),
            new CallbackDataSender("Кэф слова", new CallbackData(EditSettingsCommand.NAME, "word")),
            new CallbackDataSender("Нижняя граница совпадения", new CallbackData(EditSettingsCommand.NAME, "lower"))
    }};

    @CommandFirst
    public void showSettings(TelegramLongPollingEngine engine,
                             @ParamName("chatId") Long chatId){
        if (chatId.equals(botOwnerId)) {
            String response = String.format(
                    CURRENT_SETTINGS,
                    searchSettings.getSegmentForShort().getStart(), searchSettings.getSegmentForShort().getEnd(), df.format(searchSettings.getCoefForShortMessage()),
                    searchSettings.getSegmentForMiddle().getStart(), searchSettings.getSegmentForMiddle().getEnd(), df.format(searchSettings.getCoefForMiddleMessage()),
                    searchSettings.getSegmentForLong().getStart(), df.format(searchSettings.getCoefForLongMessage()),
                    df.format(searchSettings.getCoefForCurrentWord()),
                    df.format(searchSettings.getCoefForLowerLimit())
            );
            MessageOperations.sendNewMessage(
                    chatId,
                    response,
                    ParseMode.MARKDOWN,
                    KeyboardUtil.inline(buttons),
                    engine
            );
        }
    }

    public SearchSettingsCommand(SearchSettings searchSettings) {
        this.searchSettings = searchSettings;
    }
}
