package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    List<Film> getFilms();

    Film getFilm(Long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getTopFilms(Long count);

    List<Film> getTopFilms(Long count, Integer genreId);

    List<Film> getTopFilms(Long count, String year);

    List<Film> getTopFilms(Long count, Integer genreId, String year);

    void addLike(Long idFilm, Long idUser);

    void removeLike(Long idFilm, Long idUser);
}
