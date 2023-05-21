package ru.yandex.practicum.filmorate.storage.impl.memory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Repository("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long id = 1;

    @Override
    public Optional<List<Film>> getFilms() {
        return Optional.of(new ArrayList<>(films.values()));
    }

    @Override
    public Optional<Film> getFilm(Long id) {
        if (films.containsKey(id)) {
            return Optional.of(films.get(id));
        }

        return Optional.empty();
    }

    @Override
    public Optional<Film> addFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            film.setId(id);
            films.put(film.getId(), film);
            id++;
            return Optional.of(film);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return Optional.of(film);
        }

        return Optional.empty();
    }
}
