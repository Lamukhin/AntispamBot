package com.lamukhin.AntispamBot.service.interfaces;

import com.lamukhin.AntispamBot.db.entity.AdminEntity;

public interface AdminService {

    boolean hasAdminStatusByUserId(long userId);

    AdminEntity findByUserId(Long userId);

    void saveNewAdmin(long userId);

    void updatePermissions(long userId);
}
