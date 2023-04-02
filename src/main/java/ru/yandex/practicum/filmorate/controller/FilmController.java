package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    int id = 1;

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    @ResponseBody
    public Film addFilm(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            throw new ValidationException("Такой фильм уже существует");
        } else {
            film.setId(id);
            films.put(film.getId(), film);
            id++;
        }

        return film;
    }

    @PutMapping
    @ResponseBody
    public Film updateFilm(@Valid @RequestBody Film film) {
        if(films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new ValidationException("Такого фильма не существует");
        }


        return film;
    }
}
