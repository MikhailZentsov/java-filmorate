package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InMemoryFilmService implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public InMemoryFilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @Override
    public Film getFilm(Long id) {
        return filmStorage.getFilm(id);
    }

    @Override
    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    @Override
    public List<Film> getTopFilms(Long count) {
        if (count == null) {
            count = 10L;
        }
        return filmStorage.getFilms().stream()
                .sorted(Collections.reverseOrder(Comparator.comparingInt(film -> film.getLikes().size())))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Film addLike(Long idFilm, Long idUser) {
        User user = userStorage.getUser(idUser);
        Film film = filmStorage.getFilm(idFilm);
        film.getLikes().add(user.getId());

        return film;
    }

    @Override
    public Film removeLike(Long idFilm, Long idUser) {
        User user = userStorage.getUser(idUser);
        Film film = filmStorage.getFilm(idFilm);
        film.getLikes().remove(user.getId());

        return film;
    }
}
