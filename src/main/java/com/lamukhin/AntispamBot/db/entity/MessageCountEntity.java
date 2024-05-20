package com.lamukhin.AntispamBot.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "message_count")
public class MessageCountEntity {

    @Id
    private int id;
    @Column(name="id_chat_telegram")
    private long idChatTelegram;
    private long counter;

    public MessageCountEntity(long idChatTelegram, long counter) {
        this.idChatTelegram = idChatTelegram;
        this.counter = counter;
    }

    public int getId() {
        return id;
    }

    public long getIdChatTelegram() {
        return idChatTelegram;
    }

    public long getCounter() {
        return counter;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdChatTelegram(long idChatTelegram) {
        this.idChatTelegram = idChatTelegram;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }
}
