package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

@Getter
public enum EventOperation {
    REMOVE("REMOVE"),
    ADD("ADD"),
    UPDATE("UPDATE");

    private final String name;

    EventOperation(String name) {
        this.name = name;
    }
}
