package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Получена сущность Film");
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Получена сущность Film");
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Long id) {
        return filmService.getFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopulars(@RequestParam(defaultValue = "10", required = false) Long count,
                                      @RequestParam(required = false) Integer genreId,
                                      @RequestParam(required = false) String year) {
        return filmService.getTopFilms(count, genreId, year);
    }

    @GetMapping("/search")
    public List<Film> getFilmsWithQueryByTitleAndDirector(
            @RequestParam @NotBlank String query,
            @RequestParam List<String> by) {
        return filmService.getFilmsWithQueryByTitleAndDirector(query, by);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getSortedFilmsByDirector(@PathVariable("directorId") Long directorId,
                                              @RequestParam("sortBy") String sort) {
        return filmService.getFilmsByDirectorSortedBy(directorId, sort);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam(name = "userId") long userId,
                                     @RequestParam(name = "friendId") long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @DeleteMapping("{filmId}")
    public void deleteFilm(@PathVariable("filmId") long filmId) {
        filmService.deleteFilmById(filmId);
    }
}
