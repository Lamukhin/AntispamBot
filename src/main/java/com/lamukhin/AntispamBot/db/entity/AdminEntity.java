package com.lamukhin.AntispamBot.db.entity;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "admin")
public class AdminEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(name="user_telegram_id")
    private long userId;
    @Column(name="is_active")
    private boolean isActive;

    public AdminEntity(long userId) {
        this.userId = userId;
        this.isActive = true;
    }

    public AdminEntity() {}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdminEntity that = (AdminEntity) o;
        return userId == that.userId && isActive == that.isActive && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, isActive);
    }

    public long getUserId() {
        return userId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
