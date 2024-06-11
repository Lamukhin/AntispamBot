package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.algorithm.SearchSettings;
import com.lamukhin.AntispamBot.util.MessageOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
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
    private final Logger log = LoggerFactory.getLogger(SearchSettingsCommand.class);
    public static final String NAME = "/settings";
    private final SearchSettings searchSettings;
    private final DecimalFormat df = new DecimalFormat("#.##");
    final CallbackDataSender[][] buttons = {{
            new CallbackDataSender("Короткое", new CallbackData(EditSettingsCommand.NAME, "short")),
            new CallbackDataSender("Среднее", new CallbackData(EditSettingsCommand.NAME, "middle")),
            new CallbackDataSender("Длинное", new CallbackData(EditSettingsCommand.NAME, "long")),
            new CallbackDataSender("Кэф слова", new CallbackData(EditSettingsCommand.NAME, "word"))
    }};

    @CommandFirst
    public void showSettings(TelegramLongPollingEngine engine,
                             @ParamName("chatId") Long chatId){
        if(chatId == 260113861L) {
            String response = String.format(
                    CURRENT_SETTINGS,
                    searchSettings.getSegmentForShort().getStart(), searchSettings.getSegmentForShort().getEnd(), df.format(searchSettings.getCoefForShortMessage()),
                    searchSettings.getSegmentForMiddle().getStart(), searchSettings.getSegmentForMiddle().getEnd(), df.format(searchSettings.getCoefForMiddleMessage()),
                    searchSettings.getSegmentForLong().getStart(), searchSettings.getSegmentForLong().getEnd(), df.format(searchSettings.getCoefForLongMessage()),
                    df.format(searchSettings.getCoefForCurrentWord())
            );
            MessageOperations.sendNewMessage(
                    chatId,
                    response,
                    KeyboardUtil.inline(buttons),
                    engine
            );
        }
    }

    public SearchSettingsCommand(SearchSettings searchSettings) {
        this.searchSettings = searchSettings;
    }
}