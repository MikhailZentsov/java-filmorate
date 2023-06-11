package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class DbFilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    private final DirectorStorage directorStorage;

    public DbFilmServiceImpl(FilmStorage filmStorage,
                             UserStorage userStorage,
                             DirectorStorage directorStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.directorStorage = directorStorage;
    }

    @Override
    public List<Film> getFilms() {
        return filmStorage.findAll().orElse(new ArrayList<>());
    }

    @Override
    public Film getFilm(Long id) {
        return filmStorage.getById(id).orElseThrow(() -> new FilmNotFoundException(String.format(
                "Фильм с ID %s не найден", id)));
    }

    @Override
    public Film addFilm(Film film) {
        return filmStorage.saveOne(film).orElseThrow(() -> new FilmAlreadyExistsException(String.format(
                "Фильм с ID %s уже существует", film.getId())));
    }

    @Override
    public Film updateFilm(Film film) {
        return filmStorage.updateOne(film).orElseThrow(() -> new FilmNotFoundException(String.format(
                "Фильм с ID %s не найден", film.getId())));
    }

    @Override
    public List<Film> getTopFilms(Long count) {
        if (count == null) {
            count = 10L;
        }
        return filmStorage.getPopularFilms(count);
    }

    @Override
    public void addLike(Long idFilm, Long idUser) {
        userStorage.getById(idUser).orElseThrow(() -> new UserNotFoundException(String.format(
                "Пользователь с ID %s не найден", idUser)));
        filmStorage.getById(idFilm).orElseThrow(() -> new FilmNotFoundException(String.format(
                "Фильм с ID %s не найден", idFilm)));
        filmStorage.creatLike(idFilm, idUser);
    }

    @Override
    public void removeLike(Long idFilm, Long idUser) {
        userStorage.getById(idUser).orElseThrow(() -> new UserNotFoundException(String.format(
                "Пользователь с ID %s не найден", idUser)));
        filmStorage.getById(idFilm).orElseThrow(() -> new FilmNotFoundException(String.format(
                "Фильм с ID %s не найден", idFilm)));
        filmStorage.removeLike(idFilm, idUser);
    }

    @Override
    public List<Film> getFilmsWithQueryByTitleAndDirector(String query, List<String> by) {
        List<String> listBy = new ArrayList<>();
        listBy.add("title");
        listBy.add("director");

        by.retainAll(listBy);

        return filmStorage.findFilmsByNameAndDirector(query, by);
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    @Override
    public List<Film> getFilmsByDirectorSortedBy(Long directorId, String sort) {
        if (sort.equals("likes")) {
            return filmStorage.getFilmsByDirectorSortedByLikes(directorId, sort).orElseThrow(() ->
                    new DirectorNotFoundException(String.format(
                            "Директор с ID %s не существует", directorId)));
        } else if (sort.equals("year")) {
            return filmStorage.getFilmsByDirectorSortedByYear(directorId, sort).orElseThrow(() ->
                    new DirectorNotFoundException(String.format(
                            "Директор с ID %s не существует", directorId)));
        } else {
            throw new ValidationParamsException("Параметр sort должен быть либо likes, либо year");
        }
    }
}
