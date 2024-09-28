package com.lamukhin.AntispamBot.db.entity;

import jakarta.persistence.*;

import java.util.Objects;
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

    public String getWord() {
        return word;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DictionaryEntity that = (DictionaryEntity) o;
        return value == that.value && Objects.equals(id, that.id) && Objects.equals(word, that.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, word, value);
    }
}
