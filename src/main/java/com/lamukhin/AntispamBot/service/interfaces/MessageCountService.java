package com.lamukhin.AntispamBot.service.interfaces;

public interface MessageCountService {

    Long amountOfMessages(long userId);

    void saveNewMember(long userId);

    void updateAmount(long userId);
}
