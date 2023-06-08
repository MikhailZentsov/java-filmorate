package ru.yandex.practicum.filmorate.controller;

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
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
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

    /*@GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(required = false) Long count) {
        return filmService.getTopFilms(count);
    }*/
    //GET /films/popular?count={limit}&genreId={genreId}&year={year}
   /* @GetMapping("/popular")
    public Long getMostPopulars(@RequestParam(required = false) Long count,
                                @RequestParam(required = false) Integer genreId,
                                @RequestParam(required = false) Integer year) {
        Long result = 0L;
        if (count != null) {
            result += count;
        }
        if (genreId != null) {
            result += genreId;
        }
        if (year != null) {
            result += year;
        }
        return result;
    }*/

    @GetMapping(value = "/popular", params = {"gid", "ye"})
    public String getStuff(@RequestParam(value = "gid") String gid,
                           @RequestParam(value = "ye", required = false) String ye) {

        return gid + ye;
    }

    @GetMapping(value = "/popular", params = {"gid"})
    public String getStuff(@RequestParam(value = "gid") String gid) {

        // do stuff for bar
        return gid;
    }

    /*@GetMapping(value = "/popular", params = {"foo", "bar"})
    public String getStuff(@RequestParam(value = "foo") String foo,
                           @RequestParam(value = "bar", required = false) String bar) {

        return foo + bar;
    }

    @GetMapping(value = "/popular", params = {"bar"})
    public String getStuff(@RequestParam(value = "bar") String bar) {

        // do stuff for bar
        return bar;
    }*/

}
