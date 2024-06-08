package com.lamukhin.AntispamBot.util;

public class ResponseMessage {
    public static final String HELLO = "Привет! Я бот-защитник от криптоспама.\n"
            + "Удалено сообщений: %s\n"
            + "Забанено спамеров: %s\n"
            + "Дата запуска в работу: %s\n"
            + "Фидбек писать @ytrewq112233";
    public static final String DONT_FLOOD = "Флуд до добра не доводит! Я немногим ранее уже отвечал, "
            + "сообщение наверняка осталось где-то \"выше\".";

    public static final String SPAM_FOUND = "Скорее всего, это спам. \"Вхождение\" сообщения в словарь банвордов более %d %%.";
    public static final String MAYBE_SPAM = "Подозреваю, это спам.\n @ytrewq112233, проверь и пингани админа.";
    public static final String NEW_WORDS_SAVED = "Сохранил новые слова:\n%s\nСлов в словаре: %d";
    public static final String SEND_WORDS_TO_SAVE = "Отправьте сообщение, которое сохранится в словарь";
}
