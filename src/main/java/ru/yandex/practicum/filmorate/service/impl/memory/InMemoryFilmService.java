package ru.yandex.practicum.filmorate.service.impl.memory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service("InMemoryFilmService")
public class InMemoryFilmService implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public InMemoryFilmService(@Qualifier("InMemoryFilmStorage") FilmStorage filmStorage,
                               @Qualifier("InMemoryUserStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public List<Film> getFilms() {
        return filmStorage.getFilms().orElse(new ArrayList<>());
    }

    @Override
    public Film getFilm(Long id) {
        return filmStorage.getFilm(id).orElseThrow(() -> new FilmNotFoundException(String.format(
                "Фильм с ID %s не найден", id
        )));
    }

    @Override
    public Film addFilm(Film film) {
        return filmStorage.addFilm(film).orElseThrow(() -> new FilmAlreadyExistsException(String.format(
                "Фильм с ID %s уже существует", film.getId()
        )));
    }

    @Override
    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film).orElseThrow(() -> new FilmNotFoundException(String.format(
                "Фильм с ID %s не найден", film.getId()
        )));
    }

    @Override
    public List<Film> getTopFilms(Long count) {
        if (count == null) {
            count = 10L;
        }
        return filmStorage.getFilms().orElse(new ArrayList<>()).stream()
                .sorted(Collections.reverseOrder(Comparator.comparingInt(film -> film.getLikes().size())))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Film addLike(Long idFilm, Long idUser) {
        User user = userStorage.getUser(idUser).orElseThrow(() -> new UserNotFoundException(String.format(
                "Пользователь с ID %s не найден", idUser
        )));
        Film film = filmStorage.getFilm(idFilm).orElseThrow(() -> new FilmNotFoundException(String.format(
                "Фильм с ID %s не найден", idFilm
        )));
        film.getLikes().add(user.getId());
        filmStorage.updateFilm(film);

        return film;
    }

    @Override
    public Film removeLike(Long idFilm, Long idUser) {
        User user = userStorage.getUser(idUser).orElseThrow(() -> new UserNotFoundException(String.format(
                "Пользователь с ID %s не найден", idUser
        )));
        Film film = filmStorage.getFilm(idFilm).orElseThrow(() -> new FilmNotFoundException(String.format(
                "Фильм с ID %s не найден", idFilm
        )));
        film.getLikes().remove(user.getId());
        filmStorage.updateFilm(film);

        return film;
    }
}
