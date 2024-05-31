package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.db.entity.MessageCountEntity;
import com.lamukhin.AntispamBot.db.repo.MessageCountRepo;
import com.lamukhin.AntispamBot.service.interfaces.MessageCountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageCountServiceDefault implements MessageCountService {

    private final Logger log = LoggerFactory.getLogger(MessageCountServiceDefault.class);
    private final MessageCountRepo messageCountRepo;
    private Map<String, MessageCountEntity> cachedUsers = new HashMap<>();

    @PostConstruct
    private void fetchUsersFromDatabase(){
        List<MessageCountEntity> result = messageCountRepo.findAll();
        if (result.isEmpty()) {
            log.warn("There is no data in Database, cache is empty now.");
        } else {
            result.forEach(element ->
                    cachedUsers.put(
                            String.valueOf(element.getIdChatTelegram()),
                            element
                    )
            );
            log.warn("Data has successfully fetched from the Database, {} records found.", cachedUsers.size());
        }
    }

    @Override
    public Integer amountOfMessages(long idChatTelegram) {
        MessageCountEntity user = cachedUsers.get(String.valueOf(idChatTelegram));
        if (user == null){
            return null;
        }
        //TODO: разобраться с long и int. Нахер нам и не нужон этот ваш лонг!
        return Math.toIntExact(user.getCounter());
    }

    @Override
    public void saveNewMember(long idChatTelegram) {
        MessageCountEntity user = new MessageCountEntity(
                idChatTelegram,
                1
        );
        cachedUsers.put(String.valueOf(idChatTelegram), user);
    }

    @Override
    public void updateAmount(long idChatTelegram) {
        MessageCountEntity user = cachedUsers.get(String.valueOf(idChatTelegram));
        user.setCounter(user.getCounter() + 1);
        cachedUsers.put(String.valueOf(idChatTelegram), user);
    }

    @Scheduled(fixedDelay = 20 * 1000) //every hour
    protected void saveCacheToDatabase() {
        cachedUsers.forEach((key, value) ->
                messageCountRepo.save(value)
        );
        log.warn("Cached users successfully saved to DB {}", cachedUsers.size());
    }

    @PreDestroy
    private void saveAllDataToDb(){
        saveCacheToDatabase();
        log.warn("Saving all the data before destroying the bean \"MessageCountService\"");
    }


    public MessageCountServiceDefault(MessageCountRepo messageCountRepo) {
        this.messageCountRepo = messageCountRepo;
    }
}
