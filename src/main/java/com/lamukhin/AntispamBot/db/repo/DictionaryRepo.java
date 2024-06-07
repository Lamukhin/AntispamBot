package com.lamukhin.AntispamBot.db.repo;

import com.lamukhin.AntispamBot.db.entity.DictionaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DictionaryRepo extends JpaRepository<DictionaryEntity, UUID> {
}
