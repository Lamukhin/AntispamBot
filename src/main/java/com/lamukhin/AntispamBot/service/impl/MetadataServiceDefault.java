package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.command.SaveNewBanwordsCommand;
import com.lamukhin.AntispamBot.db.entity.MetadataEntity;
import com.lamukhin.AntispamBot.db.repo.MetadataRepo;
import com.lamukhin.AntispamBot.service.interfaces.MetadataService;
import org.springframework.stereotype.Service;

@Service
public class MetadataServiceDefault implements MetadataService {

    private final MetadataRepo metadataRepo;


    @Override
    public MetadataEntity findMetadata(String botName) {
        return metadataRepo.getByBotName(botName);
    }

    @Override
    public MetadataEntity saveMetadata(MetadataEntity newBot) {
       return metadataRepo.save(newBot);
    }

    @Override
    public void updateBannedUsers(String botName) {
        MetadataEntity metadataEntity = metadataRepo.getByBotName(botName);
        if (metadataEntity != null) {
            int incr = metadataEntity.getUsersBanned() + 1;
            metadataEntity.setUsersBanned(incr);
            metadataRepo.save(metadataEntity);
        }
    }

    @Override
    public void updateDeletedMessages(String botName) {
        MetadataEntity metadataEntity = metadataRepo.getByBotName(botName);
        if (metadataEntity != null) {
            int incr = metadataEntity.getMessagesDeleted() + 1;
            metadataEntity.setMessagesDeleted(incr);
            metadataRepo.save(metadataEntity);
        }
    }

    public MetadataServiceDefault(MetadataRepo metadataRepo) {
        this.metadataRepo = metadataRepo;
    }
}
