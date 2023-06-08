package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class DbFilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public DbFilmServiceImpl(FilmStorage filmStorage,
                             UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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
    public void deleteFilmById(long filmId) {
        filmStorage.deleteFilmById(filmId).orElseThrow(()->new FilmNotFoundException(String.format(
                "Фильм с ID %s не найден", filmId)));
    }
}
