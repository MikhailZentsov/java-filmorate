package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    List<Film> getFilms();

    Film getFilm(Long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getTopFilms(Long count, Integer genreId, String year);

    void addLike(Long idFilm, Long idUser, Integer rate);

    void removeLike(Long idFilm, Long idUser);

    List<Film> getFilmsWithQueryByTitleAndDirector(String query, List<String> by);

    List<Film> getFilmsByDirectorSortedBy(Long directorId, String sort);

    List<Film> getCommonFilms(Long userId, Long friendId);

    void deleteFilmById(long filmId);
}
