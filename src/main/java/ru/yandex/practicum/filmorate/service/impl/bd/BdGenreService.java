package ru.yandex.practicum.filmorate.service.impl.bd;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.ArrayList;
import java.util.List;

@Service("BdGenreService")
public class BdGenreService implements GenreService {
    private final GenreStorage genreStorage;

    public BdGenreService(@Qualifier("BdGenreStorage") GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @Override
    public List<Genre> getGenres() {
        return genreStorage.getGenres().orElse(new ArrayList<>());
    }

    @Override
    public Genre getGenre(Integer id) {
        return genreStorage.getGenre(id).orElseThrow(() ->
                new GenreNotFoundException(String.format(
                        "Жанр с ID %s не найден", id
                )));
    }
}
