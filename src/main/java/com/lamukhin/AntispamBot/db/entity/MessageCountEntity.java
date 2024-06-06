package com.lamukhin.AntispamBot.db.entity;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "message_count")
public class MessageCountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;



    @Column(name="id_chat_telegram")
    private long idChatTelegram;
    @Column(name="counter")
    private long counter;

    public MessageCountEntity() {}

    public MessageCountEntity(long idChatTelegram, long counter) {
        this.idChatTelegram = idChatTelegram;
        this.counter = counter;
    }

    public UUID getId() {
        return id;
    }

    public long getIdChatTelegram() {
        return idChatTelegram;
    }

    public long getCounter() {
        return counter;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setIdChatTelegram(long idChatTelegram) {
        this.idChatTelegram = idChatTelegram;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageCountEntity that = (MessageCountEntity) o;
        return idChatTelegram == that.idChatTelegram && counter == that.counter && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idChatTelegram, counter);
    }
}
