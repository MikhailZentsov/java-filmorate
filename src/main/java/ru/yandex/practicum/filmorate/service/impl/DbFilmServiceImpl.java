package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DbFilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final EventService eventService;
    private final DirectorService directorService;

    @Override
    public List<Film> getFilms() {
        return filmStorage.findAll();
    }

    @Override
    public Film getFilm(Long id) {
        return filmStorage.getById(id).orElseThrow(() ->
                new NotFoundException(String.format(
                        "Фильм с ID %s не найден", id)));
    }

    @Override
    public Film addFilm(Film film) {
        return filmStorage.saveOne(film).orElseThrow(() ->
                new AlreadyExistsException(String.format(
                        "Фильм с ID %s уже существует", film.getId())));
    }

    @Override
    public Film updateFilm(Film film) {
        return filmStorage.updateOne(film).orElseThrow(() ->
                new NotFoundException(String.format(
                        "Фильм с ID %s не найден", film.getId())));
    }

    @Override
    public List<Film> getTopFilms(Long count, Integer genreId, String year) {
        /*if (genreId != null && year != null) {
            log.info("Получение фильмов с отбором по жанру и году.");
            return filmStorage.getPopularFilms(count, genreId, year);
        } else if (genreId == null && year == null) {
            log.info("Получение фильмов без отбора.");
            return filmStorage.getPopularFilms(count);
        } else if (genreId != null) {
            log.info("Получение фильмов с отбором по жанру.");
            return filmStorage.getPopularFilms(count, genreId);
        } else {
            log.info("Получение  с отбором по году");
            return filmStorage.getPopularFilms(count, year);
        }*/
        return filmStorage.getPopularFilms(count, genreId, year);
    }

    @Override
    public void addLike(Long idFilm, Long idUser, Integer rate) {
        userStorage.getById(idUser).orElseThrow(() ->
                new NotFoundException(String.format(
                        "Пользователь с ID %s не найден", idUser)));
        filmStorage.getById(idFilm).orElseThrow(() ->
                new NotFoundException(String.format(
                        "Фильм с ID %s не найден", idFilm)));

        filmStorage.creatLike(idFilm, idUser, rate);
        eventService.createAddLikeEvent(idUser, idFilm);
    }

    @Override
    public void removeLike(Long idFilm, Long idUser) {
        userStorage.getById(idUser).orElseThrow(() ->
                new NotFoundException(String.format(
                        "Пользователь с ID %s не найден", idUser)));
        filmStorage.getById(idFilm).orElseThrow(() ->
                new NotFoundException(String.format(
                        "Фильм с ID %s не найден", idFilm)));

        filmStorage.removeLike(idFilm, idUser);
        eventService.createRemoveLikeEvent(idUser, idFilm);
    }

    @Override
    public List<Film> getFilmsWithQueryByTitleAndDirector(String query, List<String> by) {
        List<String> listBy = new ArrayList<>();
        listBy.add("title");
        listBy.add("director");

        by.retainAll(listBy);
        log.info("Отсечены невалидные параметры в типе отборов. Остаются только {} и {} ",
                "title",
                "director");

        return filmStorage.findFilmsByNameAndDirector(query, by);
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    @Override
    public List<Film> getFilmsByDirectorSortedBy(Long directorId, String sort) {
        directorService.getDirector(directorId);

        if (sort.equals("likes")) {
            log.info("Получения фильмов режиссера с сортировкой по лайкам.");
            return filmStorage.getFilmsByDirectorSortedByLikes(directorId);
        } else if (sort.equals("year")) {
            log.info("Получения фильмов режиссера с сортировкой по году.");
            return filmStorage.getFilmsByDirectorSortedByYear(directorId);
        } else {
            log.info("Параметр sort = {}. Должен быть либо likes, либо year", sort);
            throw new ValidationParamsException("Параметр sort должен быть либо likes, либо year");
        }
    }

    @Override
    public void deleteFilmById(long filmId) {
        filmStorage.deleteFilmById(filmId).orElseThrow(() ->
                new NotFoundException(String.format(
                        "Фильм с ID %s не найден", filmId)));
    }
}
