package com.lamukhin.AntispamBot.service.interfaces;

import com.lamukhin.AntispamBot.db.entity.MetadataEntity;

public interface MetadataService {

    MetadataEntity findMetadata(String botName);
    MetadataEntity saveMetadata(MetadataEntity newBot);
    void updateBannedUsers(String botName);
    void updateDeletedMessages(String botName);
}
