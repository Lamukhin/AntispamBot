package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.db.entity.MessageCountEntity;
import com.lamukhin.AntispamBot.db.repo.MessageCountRepo;
import com.lamukhin.AntispamBot.service.interfaces.MessageCountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
//@EnableAsync
public class MessageCountServiceDefault implements MessageCountService {

    private final Logger log = LoggerFactory.getLogger(MessageCountServiceDefault.class);
    private final MessageCountRepo messageCountRepo;
    private Map<String, Integer> cachedUsers = new HashMap<>();

    @PostConstruct
    private void fetchUsersFromDatabase(){
        List<MessageCountEntity> result = messageCountRepo.findAll();
        if (result.isEmpty()) {
            log.warn("There is no data in Database, cache is empty now.");
        } else {
            result.forEach(element ->
                    cachedUsers.put(
                            String.valueOf(element.getIdChatTelegram()),
                            (int) element.getCounter()
                    )
            );
            log.warn("Data has successfully fetched from the Database, {} records found.", cachedUsers.size());
        }
    }

    @Override
    public Integer amountOfMessages(long idChatTelegram) {
        return cachedUsers.get(String.valueOf(idChatTelegram));
    }

    @Override
    public void saveNewMember(long idChatTelegram) {
        cachedUsers.put(String.valueOf(idChatTelegram), 1);
    }

    @Override
    public void updateAmount(long userTelegramId) {
        int increasedAmount = cachedUsers.get(String.valueOf(userTelegramId)) + 1;
        cachedUsers.put(String.valueOf(userTelegramId), increasedAmount);
    }

    //@Async
    @Scheduled(fixedDelay = 60 * 60 * 1000) //every hour
    protected void saveCacheToDatabase() {
        cachedUsers.forEach((key, value) ->
                messageCountRepo.save(
                        new MessageCountEntity(
                                Long.parseLong(key),
                                value
                        )
                )
        );
    }


    public MessageCountServiceDefault(MessageCountRepo messageCountRepo) {
        this.messageCountRepo = messageCountRepo;
    }
}
