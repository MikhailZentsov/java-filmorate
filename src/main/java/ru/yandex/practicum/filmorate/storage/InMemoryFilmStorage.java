package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Repository
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long id = 1;

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(Long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new FilmNotFoundException(String.format(
                    "Фильм с ID %s не найден",
                    id
            ));
        }
    }

    @Override
    public Film addFilm(Film film) {
        if (films.containsKey(film.getId())) {
            throw new FilmAlreadyExistsException(String.format(
                    "Фильм с ID %s уже существует",
                    film.getId()
            ));
        } else {
            film.setId(id);
            films.put(film.getId(), film);
            id++;
            return film;
        }
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new FilmNotFoundException(String.format(
                    "Фильм с ID %s не найден",
                    film.getId()
            ));
        }
    }
}
