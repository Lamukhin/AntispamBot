package com.lamukhin.AntispamBot.util;

public final class ResponseMessage {
    public static final String BOT_FULL_INFO = """
            Привет! Я бот-защитник от криптоспама.
            Статус: *%s* (%s %s %s по МСК)
            Админ, пиши `!spam` ответом на сообщение спамера, если я его не заметил.
            Удалено сообщений: %s
            Забанено спамеров: %s
            Слов в словаре банвордов: %s
            Я родился %s! 🐣
            Фидбек писать [сюда](tg://user?id=260113861)""";

    public static final String SPAM_FOUND = "Скорее всего, это спам. \"Вхождение\" сообщения в словарь банвордов около %d %%. %s";
    public static final String MAYBE_SPAM = """
            Подозреваю, это спам.
            @ytrewq112233, проверь и пингани админа.
            Если это читает админ, то зареплай `!spam` на сообщение спамера.
            %s""";
    public static final String NEW_WORDS_SAVED = "Сохранил новые слова:\n%s\nСлов в словаре: %d";
    public static final String SEND_WORDS_TO_SAVE = "Отправьте сообщение, которое сохранится в словарь";
    public static final String CURRENT_SETTINGS = """
            *Настройки поиска:*
            Кэф вхождения в словарь для _короткого_ сообщения (%d - %d слов): %s
            Кэф вхождения в словарь для _среднего_ сообщения (%d - %d слов): %s
            Кэф вхождения в словарь для _длинного_ сообщения (от %d слов): %s
            Кэф совпадения символов слова со словарём: %s
            Кэф нижней границы совпадения со словарём: %s
            *Редактировать:*""";
    public static final String HELLO = "Привет, %s!\nЖми меню, там вся инфа.";
    public static final String SEND_LENGTH_VALUES = "Пришлите новые значения для _%s_ сообщений через пробел в формате 5 10 0.5";
    public static final String SAVED_LENGTH_VALUES = "Данные для коротких _%s_ сохранены. /settings, чтобы убедиться";
    public static final String SEND_WORD_COEF = "Пришлите новое значение кэфа для одного слова в формате 0.5";
    public static final String SEND_LOWER_LIMIT_COEF = "Пришлите новое значение кэфа нижней границы спама в формате 0.5";
    public static final String SAVED_WORD_COEF = "Кэф для каждого слова изменён. /settings, чтобы убедиться";
    public static final String SAVED_LOWER_LIMIT_COEF = "Кэф нижней границы совпадения изменён. /settings, чтобы убедиться";
    public static final String SAVED_NEW_ADMIN = "Получили TG ID %s и имя \'%s\' юзера и сохранили нового админа.";
    public static final String SEND_NEW_CANDIDATE = "Отправьте сообщение от кандидата в админы или его userId и имя через пробел";
    public static final String LIST_OF_ADMINS = "Список админов группы:\n%s\nСкопируйте и отправьте ID человека, которого хотите исключить из админов.";
    public static final String ADMIN_REMOVED = "У \'%s\' больше нет прав администратора.";
    public static final String ERROR_ADDING_ADMIN = "Что-то пошло не так. Возможно, настройки приватности пользователя не позволяют получить ID через пересланное сообщение.";
    public static final String ERROR_PROCESSING_MESSAGE = "Что-то пошло не так. Проверьте корректность ответа и попробуйте снова.";
    public static final String YOUR_STATUS = """
            Ваш статус \"Администратор\" *%sактивен*.
            Доступные команды:
            %s
            """;

}
