package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    List<Film> getFilms();

    Film getFilm(Long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getPopularFilms(Long count, Integer genreId, Integer year);

    void addLike(Long idFilm, Long idUser, Integer rate);

    void removeLike(Long idFilm, Long idUser);

    List<Film> getFilmsWithQueryByTitleAndDirector(String query, List<String> by);

    List<Film> getFilmsByDirectorOrderBy(Long directorId, String order);

    List<Film> getCommonFilms(Long userId, Long friendId);

    List<Film> getRecommendations(Long userId);

    void deleteFilmById(long filmId);
}
