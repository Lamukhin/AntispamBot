package com.lamukhin.AntispamBot.db.repo;

import com.lamukhin.AntispamBot.db.entity.MessageCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageCountRepo extends JpaRepository<MessageCountEntity, Integer> {

    MessageCountEntity findByIdChatTelegram(long id);
}
