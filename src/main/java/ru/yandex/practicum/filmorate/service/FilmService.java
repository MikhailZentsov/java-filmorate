package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    List<Film> getFilms();

    Film getFilm(Long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getTopFilms(Long count);

    void addLike(Long idFilm, Long idUser);

    void removeLike(Long idFilm, Long idUser);

    void deleteFilmById(long filmId);
}
