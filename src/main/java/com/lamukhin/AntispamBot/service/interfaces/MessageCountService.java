package com.lamukhin.AntispamBot.service.interfaces;

public interface MessageCountService {

    Integer amountOfMessages(long idChatTelegram);

    void saveNewMember(long idChatTelegram);

    void updateAmount(long userTelegramId);
}
