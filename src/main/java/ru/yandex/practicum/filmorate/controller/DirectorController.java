package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/directors")
@Slf4j
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) {
        return directorService.createDirector(director);
    }

    @GetMapping
    public List<Director> getDirectors() {
        return directorService.getDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable Long id) {
        return directorService.getDirector(id);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void removeDirector(@PathVariable Long id) {
        directorService.removeDirector(id);
    }
}
