package com.lamukhin.AntispamBot.db.entity;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "dictionary")
public final class DictionaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(name="word")
    private String word;
    @Column(name="value")
    private int value;

    public DictionaryEntity(String word, int value) {
        this.word = word;
        this.value = value;
    }

    public DictionaryEntity() {
    }

    public UUID getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public int getValue() {
        return value;
    }
}
