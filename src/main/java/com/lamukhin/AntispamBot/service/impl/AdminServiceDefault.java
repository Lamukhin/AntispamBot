package com.lamukhin.AntispamBot.service.impl;

import com.lamukhin.AntispamBot.db.entity.AdminEntity;
import com.lamukhin.AntispamBot.db.repo.AdminRepo;
import com.lamukhin.AntispamBot.service.interfaces.AdminService;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<AdminEntity> findAll() {
        return adminRepo.findAll();
    }

    @Override
    public void saveNewAdmin(long userId, String fullName) {
        AdminEntity adminEntity = adminRepo.findByUserId(userId);
        if (adminEntity == null) {
            adminEntity = new AdminEntity(userId, fullName);
            adminRepo.save(adminEntity);
        } else {
            adminEntity.setActive(true);
            adminRepo.save(adminEntity);
        }
    }

    @Override
    public void removeAdminRights(long userId) {
        AdminEntity adminEntity = adminRepo.findByUserId(userId);
        if (adminEntity != null) {
            adminEntity.setActive(false);
            adminRepo.save(adminEntity);
        }
        //throw new RuntimeException(String.format("There is no Admin in database with ID %d", userId));
    }

    @Override
    public void restoreAdminRights(long userId) {
        AdminEntity adminEntity = adminRepo.findByUserId(userId);
        if (adminEntity != null) {
            adminEntity.setActive(true);
            adminRepo.save(adminEntity);
        }
        //throw new RuntimeException(String.format("There is no Admin in database with ID %d", userId));
    }

    public AdminServiceDefault(AdminRepo adminRepo) {
        this.adminRepo = adminRepo;
    }
}
