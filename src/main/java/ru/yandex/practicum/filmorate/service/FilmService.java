package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    List<Film> getFilms();

    Film getFilm(Long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getTopFilms(Long count);

    Film addLike(Long idFilm, Long idUser);

    Film removeLike(Long idFilm, Long idUser);
}
