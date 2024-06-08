package com.lamukhin.AntispamBot.db.repo;

import com.lamukhin.AntispamBot.db.entity.MetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MetadataRepo extends JpaRepository<MetadataEntity, UUID> {

    MetadataEntity getByBotName(String botName);
}
