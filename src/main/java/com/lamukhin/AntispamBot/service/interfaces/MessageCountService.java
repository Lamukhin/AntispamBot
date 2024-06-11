package com.lamukhin.AntispamBot.service.interfaces;

public interface MessageCountService {

    Long amountOfMessages(long idChatTelegram);

    void saveNewMember(long idChatTelegram);

    void updateAmount(long userTelegramId);
}
