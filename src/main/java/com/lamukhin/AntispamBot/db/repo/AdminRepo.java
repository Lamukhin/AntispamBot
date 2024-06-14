package com.lamukhin.AntispamBot.db.repo;

import com.lamukhin.AntispamBot.db.entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdminRepo extends JpaRepository<AdminEntity, UUID> {

    AdminEntity findByUserId(long userId);
}
