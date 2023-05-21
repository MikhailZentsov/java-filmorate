package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Optional<List<Film>> getFilms();

    Optional<Film> getFilm(Long id);

    Optional<Film> addFilm(Film film);

    Optional<Film> updateFilm(Film film);
}
