package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Optional<List<Film>> findAll();

    Optional<Film> getById(Long id);

    Optional<Film> saveOne(Film film);

    Optional<Film> updateOne(Film film);

    List<Film> getPopularFilms(Long count);

    void creatLike(Long idFilm, Long idUser);

    void removeLike(Long idFilm, Long idUser);
}
