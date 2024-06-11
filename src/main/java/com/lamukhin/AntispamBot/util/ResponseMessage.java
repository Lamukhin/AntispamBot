package com.lamukhin.AntispamBot.util;

public class ResponseMessage {
    public static final String BOT_STATUS = """
            Привет! Я бот-защитник от криптоспама.
            Статус: %s
            Админ, пиши !spam ответом на сообщение спамера, если я его не заметил.
            Удалено сообщений: %s
            Забанено спамеров: %s
            Я родился: %s
            Фидбек писать @ytrewq112233""";
    public static final String DONT_FLOOD = "Флуд до добра не доводит! Я немногим ранее уже отвечал, "
            + "сообщение наверняка осталось где-то \"выше\".";

    public static final String SPAM_FOUND = "Скорее всего, это спам. \"Вхождение\" сообщения в словарь банвордов более %d %%.";
    public static final String MAYBE_SPAM = "Подозреваю, это спам.\n @ytrewq112233, проверь и пингани админа.\n"
            + "Если это читает админ, то зареплай !spam на сообщение спамера.";
    public static final String NEW_WORDS_SAVED = "Сохранил новые слова:\n%s\nСлов в словаре: %d";
    public static final String SEND_WORDS_TO_SAVE = "Отправьте сообщение, которое сохранится в словарь";
    public static final String CURRENT_SETTINGS = """
            Настройки поиска:
            Кэф вхождения в словарь для короткого сообщения (%d - %d слов): %s
            Кэф вхождения в словарь для среднего сообщения (%d - %d слов): %s
            Кэф вхождения в словарь для длинного сообщения (%d - %d слов): %s
            Коэффициент совпадения символов слова со словарём: %s
            Редактировать:""";
}
