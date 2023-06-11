package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {

    void saveOne(Event event);

    List<Event> findAllById(Long idUser);
}
