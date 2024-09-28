package com.lamukhin.AntispamBot.db.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "metadata")
public final class MetadataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(name="bot_name")
    private String botName;
    @Column(name="messages_deleted")
    private int messagesDeleted;
    @Column(name="users_banned")
    private int usersBanned;
    @Column(name="date_start")
    private LocalDate dateStart;

    public MetadataEntity(String botName) {
        this.botName = botName;
        messagesDeleted = 0;
        usersBanned = 0;
        dateStart = LocalDate.now();
    }

    public MetadataEntity() {
    }

    public String getBotName() {
        return botName;
    }

    public int getMessagesDeleted() {
        return messagesDeleted;
    }

    public int getUsersBanned() {
        return usersBanned;
    }

    public LocalDate getDateStart() {
        return dateStart;
    }

    public void setMessagesDeleted(int messagesDeleted) {
        this.messagesDeleted = messagesDeleted;
    }

    public void setUsersBanned(int usersBanned) {
        this.usersBanned = usersBanned;
    }
}
