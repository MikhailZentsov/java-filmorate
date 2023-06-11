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

    List<Film> getPopularFilms(Long count, Integer genreId);

    List<Film> getPopularFilms(Long count, String year);

    List<Film> getPopularFilms(Long count, Integer genreId, String year);

    void creatLike(Long idFilm, Long idUser);

    void removeLike(Long idFilm, Long idUser);

    List<Film> findFilmsByNameAndDirector(String query, List<String> by);

    Optional<List<Film>> getFilmsByDirectorSortedByYear(Long directorId, String sort);

    Optional<List<Film>> getFilmsByDirectorSortedByLikes(Long directorId, String sort);

    List<Film> getCommonFilms(Long userId, Long friendId);

    Optional<Film> deleteFilmById(long filmId);
}
