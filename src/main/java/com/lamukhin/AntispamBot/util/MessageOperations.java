package com.lamukhin.AntispamBot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.wdeath.managerbot.lib.bot.TelegramLongPollingEngine;

public class MessageOperations {

    private static final Logger log = LoggerFactory.getLogger(MessageOperations.class);

    public static void sendNewMessage(Long chatId, String text, InlineKeyboardMarkup markup, TelegramLongPollingEngine engine) {
        var send = SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .build();
        try {
            engine.execute(send);
        } catch (TelegramApiException e) {
            log.error("Message operation is not executed: {}", e.getMessage());
        }
    }

    public static void sendNewMessage(Long chatId, String text, TelegramLongPollingEngine engine) {
        var send = SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .build();
        try {
            engine.execute(send);
        } catch (TelegramApiException e) {
            log.error("Message operation is not executed: {}", e.getMessage());
        }
    }

    public static void replyToMessage(Long chatId, String text, int messageId, TelegramLongPollingEngine engine) {
        var send = SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .replyToMessageId(messageId)
                .build();
        try {
            engine.execute(send);
        } catch (TelegramApiException e) {
            log.error("Message operation is not executed: {}", e.getMessage());
        }
    }

    public static void replyToMessage(Long chatId, String text, InlineKeyboardMarkup markup, int messageId, TelegramLongPollingEngine engine) {
        var send = SendMessage
                .builder()
                .chatId(chatId)
                .text(text)
                .replyToMessageId(messageId)
                .replyMarkup(markup)
                .build();
        try {
            engine.execute(send);
        } catch (TelegramApiException e) {
            log.error("Message operation is not executed: {}", e.getMessage());
        }
    }

    public static void editCurrentMessage(Long chatId, String text, InlineKeyboardMarkup markup, TelegramLongPollingEngine engine, Update update) {
        var send = EditMessageText
                .builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .build();
        try {
            engine.execute(send);
        } catch (TelegramApiException e) {
            log.error("Message operation is not executed: {}", e.getMessage());
        }
    }

    public static void editCurrentMessage(Long chatId, String text, TelegramLongPollingEngine engine, Update update) {
        var send = EditMessageText
                .builder()
                .chatId(chatId)
                .text(text)
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .build();
        try {
            engine.execute(send);
        } catch (TelegramApiException e) {
            log.error("Message operation is not executed: {}", e.getMessage());
        }
    }

    public static void deleteMessage(Long chatId, int messageId, TelegramLongPollingEngine engine) {
        var send = DeleteMessage
                .builder()
                .chatId(chatId)
                .messageId(messageId)
                .build();
        try {
            engine.execute(send);
        } catch (TelegramApiException e) {
            log.error("Message operation is not executed: {}", e.getMessage());
        }
    }
}
