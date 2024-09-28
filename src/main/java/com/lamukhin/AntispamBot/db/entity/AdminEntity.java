package com.lamukhin.AntispamBot.db.entity;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "admin")
public class AdminEntity implements Comparable<AdminEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(name = "user_telegram_id")
    private long userId;
    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "full_name")
    private String fullName;

    public AdminEntity(long userId, String fullName) {
        this.userId = userId;
        this.fullName = fullName;
        this.isActive = true;
    }

    public AdminEntity() {
    }


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

    public String getFullName() {
        return fullName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public int compareTo(@NotNull AdminEntity o) {

        if (this.isActive && !o.isActive) {
            return -1;
        } else if ((o.isActive && !this.isActive)) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "AdminEntity{" +
                "userId=" + userId +
                ", isActive=" + isActive +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
