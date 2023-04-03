package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
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
            log.warn("Такой фильм уже существует");
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Такой фильм уже существует");
        } else {
            film.setId(id);
            films.put(film.getId(), film);
            id++;
            log.info("Фильм {} добавлен", film);
        }
        return film;
    }

    @PutMapping
    @ResponseBody
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм {} добавлен", film);
        } else {
            log.warn("Такого фильма не существует");
            throw new ValidationException(HttpStatus.NOT_FOUND, "Такого фильма не существует");
        }
        return film;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidationException(MethodArgumentNotValidException exception) {
        throw new ValidationException(HttpStatus.BAD_REQUEST, exception.getMessage());
    }
}
