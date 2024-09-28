package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.db.entity.MessageCountEntity;
import com.lamukhin.AntispamBot.db.repo.MessageCountRepo;
import com.lamukhin.AntispamBot.service.interfaces.MessageCountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageCountServiceDefault implements MessageCountService {

    private final MessageCountRepo messageCountRepo;
    private Map<String, MessageCountEntity> cachedUsers = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(MessageCountServiceDefault.class);

    @PostConstruct
    private void fetchUsersFromDatabase() {
        List<MessageCountEntity> result = messageCountRepo.findAll();
        if (result.isEmpty()) {
            log.warn("There is no users in Database, cache is empty now.");
        } else {
            result.forEach(element ->
                    cachedUsers.put(
                            String.valueOf(element.getUserId()),
                            element
                    )
            );
            log.warn("Users has successfully fetched from the Database, {} records found.",
                    cachedUsers.size());
        }
    }

    @Override
    public Long amountOfMessages(long userId) {
        MessageCountEntity user = cachedUsers.get(String.valueOf(userId));
        if (user == null) {
            return null;
        }
        return user.getCounter();
    }

    @Override
    public void saveNewMember(long userId) {
        MessageCountEntity user = new MessageCountEntity(
                userId,
                1
        );
        cachedUsers.put(String.valueOf(userId), user);
    }

    @Override
    public void updateAmount(long userId) {
        MessageCountEntity user = cachedUsers.get(String.valueOf(userId));
        user.setCounter(user.getCounter() + 1);
        cachedUsers.put(String.valueOf(userId), user);
    }

    @Scheduled(fixedDelay = 30 * 60 * 1000) //every 30m
    protected void saveCacheToDatabase() {
        cachedUsers.forEach((key, value) ->
                messageCountRepo.save(value)
        );
        log.warn("Cached users successfully saved to DB {}", cachedUsers.size());
    }

    @PreDestroy
    private void saveAllDataToDb() {
        saveCacheToDatabase();
        log.warn("Saving all the data before destroying the bean \"MessageCountService\"");
    }


    public MessageCountServiceDefault(MessageCountRepo messageCountRepo) {
        this.messageCountRepo = messageCountRepo;
    }
}
