package com.lamukhin.AntispamBot.util;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.wdeath.managerbot.lib.bot.command.CommandContext;

public class MessageOperations {
    
    public static void sendNewMessage(Long chatId, String text, InlineKeyboardMarkup markup, CommandContext context) {
        var send = SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .build();
        context.getEngine().executeNotException(send);
    }
    
    public static void sendNewMessage(Long chatId, String text, CommandContext context) {
        var send = SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .build();
        context.getEngine().executeNotException(send);
    }

    public static void editCurrentMessage(Long chatId, String text, InlineKeyboardMarkup markup, CommandContext context) {
        var send = EditMessageText
                .builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .messageId(context.getUpdate().getCallbackQuery().getMessage().getMessageId())
                .build();
        context.getEngine().executeNotException(send);
    }

    public static void editCurrentMessage(Long chatId, String text, CommandContext context) {
        var send = EditMessageText
                .builder()
                .chatId(chatId)
                .text(text)
                .messageId(context.getUpdate().getCallbackQuery().getMessage().getMessageId())
                .build();
        context.getEngine().executeNotException(send);
    }
}
