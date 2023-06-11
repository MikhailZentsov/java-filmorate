package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DbGenreServiceImpl implements GenreService {
    private final GenreStorage genreStorage;

    @Override
    public List<Genre> getGenres() {
        return genreStorage.findAll();
    }

    @Override
    public Genre getGenre(Integer id) {
        return genreStorage.getById(id).orElseThrow(() ->
                new GenreNotFoundException(String.format(
                        "Жанр с ID %s не найден", id)));
    }
}
