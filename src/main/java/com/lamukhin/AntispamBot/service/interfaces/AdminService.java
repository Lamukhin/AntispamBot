package com.lamukhin.AntispamBot.service.interfaces;

import com.lamukhin.AntispamBot.db.entity.AdminEntity;

import java.util.List;

public interface AdminService {

    boolean hasAdminStatusByUserId(long userId);

    AdminEntity findByUserId(Long userId);

    List<AdminEntity> findAll();

    void saveNewAdmin(long userId, String fullName);

    void removeAdminRights(long userId);

    void restoreAdminRights(long userId);
}
