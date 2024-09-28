package com.lamukhin.AntispamBot.db.entity;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "message_count")
public final class MessageCountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(name="user_telegram_id")
    private long userId;
    @Column(name="counter")
    private long counter;

    public MessageCountEntity() {}

    public MessageCountEntity(long userId, long counter) {
        this.userId = userId;
        this.counter = counter;
    }

    public UUID getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public long getCounter() {
        return counter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageCountEntity that = (MessageCountEntity) o;
        return userId == that.userId && counter == that.counter && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, counter);
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }
}
