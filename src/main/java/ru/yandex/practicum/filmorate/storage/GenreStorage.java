package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    Optional<List<Genre>> getGenres();

    Optional<Genre> getGenre(Integer id);
}
