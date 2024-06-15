package com.lamukhin.AntispamBot.command;

import com.lamukhin.AntispamBot.service.interfaces.AdminService;
import com.lamukhin.AntispamBot.util.Commands;
import com.lamukhin.AntispamBot.util.MessageOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;
import ru.wdeath.managerbot.lib.bot.annotations.CommandFirst;
import ru.wdeath.managerbot.lib.bot.annotations.CommandNames;
import ru.wdeath.managerbot.lib.bot.annotations.ParamName;
import ru.wdeath.managerbot.lib.bot.callback.CallbackData;
import ru.wdeath.managerbot.lib.bot.callback.CallbackDataSender;
import ru.wdeath.managerbot.lib.bot.command.CommandContext;
import ru.wdeath.managerbot.lib.bot.command.TypeCommand;
import ru.wdeath.managerbot.lib.util.KeyboardUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.lamukhin.AntispamBot.util.ResponseMessage.HELLO;

@Component
@CommandNames(value = HelloCommand.NAME, type = TypeCommand.MESSAGE)
public class HelloCommand {

    public static final String NAME = "/start";
    private final AdminService adminService;
    private final Logger log = LoggerFactory.getLogger(HelloCommand.class);
    @Value("${bot_owner_tg_id}")
    private long botOwnerId;

    @CommandFirst
    public void greeting(TelegramLongPollingEngine engine,
                         @ParamName("userId") Long userId,
                         @ParamName("chatId") Long chatId,
                         CommandContext context) {
        if (chatId.equals(userId)) {
            context.getEngine().executeNotException(
                    new SetMyCommands(
                            createListOfCommands(userId),
                            new BotCommandScopeDefault(),
                            null
                    )
            );
            String userFirstName = context.getUpdate().getMessage().getFrom().getFirstName();

            MessageOperations.sendNewMessage(
                    chatId,
                    String.format(HELLO, userFirstName),
                    null,
                    engine
            );
        }
    }

    private List<BotCommand> createListOfCommands(Long userId) {
        List<BotCommand> listOfCommands = new ArrayList<>(
                Commands.getDefaultUserCommands()
        );
        if ((adminService.hasAdminStatusByUserId(userId)) || (userId.equals(botOwnerId))) {
            listOfCommands.addAll(Commands.getAdminCommands());        }
        if (userId.equals(botOwnerId)) {
            listOfCommands.addAll(Commands.getOwnerCommands());
        }
        return listOfCommands;
    }

    public HelloCommand(AdminService adminService) {
        this.adminService = adminService;
    }
}
