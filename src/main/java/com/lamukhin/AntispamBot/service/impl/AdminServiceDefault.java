package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.db.entity.AdminEntity;
import com.lamukhin.AntispamBot.db.repo.AdminRepo;
import com.lamukhin.AntispamBot.service.interfaces.AdminService;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceDefault implements AdminService {

    private final AdminRepo adminRepo;

    @Override
    public boolean hasAdminStatusByUserId(long userId) {
        AdminEntity adminEntity = adminRepo.findByUserId(userId);
        if (adminEntity != null) {
            return adminEntity.isActive();
        }
        return false;
        //throw new RuntimeException(String.format("There is no Admin in database with ID %d", userId));
    }

    @Override
    public AdminEntity findByUserId(Long userId) {
        return adminRepo.findByUserId(userId);
        //throw new RuntimeException(String.format("There is no Admin in database with ID %d", userId));
    }

    @Override
    public void saveNewAdmin(long userId) {
        AdminEntity adminEntity = adminRepo.findByUserId(userId);
        if (adminEntity == null) {
            adminEntity = new AdminEntity(userId);
            adminRepo.save(adminEntity);
        }
    }

    @Override
    public void updatePermissions(long userId) {
        AdminEntity adminEntity = adminRepo.findByUserId(userId);
        if (adminEntity != null) {
            adminEntity.setActive(!adminEntity.isActive());
            adminRepo.save(adminEntity);
        }
        //throw new RuntimeException(String.format("There is no Admin in database with ID %d", userId));
    }

    public AdminServiceDefault(AdminRepo adminRepo) {
        this.adminRepo = adminRepo;
    }
}
