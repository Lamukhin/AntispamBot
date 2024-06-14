package com.lamukhin.AntispamBot.db.repo;

import com.lamukhin.AntispamBot.db.entity.MessageCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageCountRepo extends JpaRepository<MessageCountEntity, UUID> {

    MessageCountEntity findByUserId(long id);
}
