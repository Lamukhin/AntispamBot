package com.lamukhin.AntispamBot.util;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;
import ru.wdeath.managerbot.lib.bot.command.CommandContext;

public class MessageOperations {
    
    public static void sendNewMessage(Long chatId, String text, InlineKeyboardMarkup markup, TelegramLongPollingEngine engine) {
        var send = SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .build();
        engine.executeNotException(send);
    }
    
    public static void sendNewMessage(Long chatId, String text, TelegramLongPollingEngine engine) {
        var send = SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .build();
        engine.executeNotException(send);
    }

    public static void editCurrentMessage(Long chatId, String text, InlineKeyboardMarkup markup, TelegramLongPollingEngine engine, Update update) {
        var send = EditMessageText
                .builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .build();
        engine.executeNotException(send);
    }

    public static void editCurrentMessage(Long chatId, String text, TelegramLongPollingEngine engine, Update update) {
        var send = EditMessageText
                .builder()
                .chatId(chatId)
                .text(text)
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .build();
        engine.executeNotException(send);
    }
}
