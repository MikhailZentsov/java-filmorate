package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> findAll();

    Optional<Film> getById(Long id);

    Optional<Film> saveOne(Film film);

    Optional<Film> updateOne(Film film);

    List<Film> getPopularFilms(Long count, Integer genreId, Integer year);

    void createLike(Long idFilm, Long idUser, Integer rate);

    void removeLike(Long idFilm, Long idUser);

    List<Film> findFilmsByNameAndDirector(String query, List<String> by);

    List<Film> getFilmsByDirectorOrderdBy(Long directorId, String order);

    List<Film> getCommonFilms(Long userId, Long friendId);

    Optional<Boolean> deleteFilmById(long filmId);

    List<Film> findRecommendationsFilms(Long userId);
}
