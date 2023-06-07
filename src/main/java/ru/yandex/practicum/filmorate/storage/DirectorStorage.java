package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {

    Optional<List<Director>> getDirectors();

    Optional<Director> getDirector(long id);

    Optional<Director> createDirector(Director director);

    Optional<Director> updateDirector(Director director);

    void removeDirector(long id);
}
