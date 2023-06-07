package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DirectorAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class DbDirectorServiceImpl implements DirectorService {

    private final DirectorStorage directorStorage;

    public DbDirectorServiceImpl(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    @Override
    public List<Director> getDirectors() {
        return directorStorage.getDirectors().orElse(new ArrayList<>());
    }

    @Override
    public Director getDirector(long id) {
        return directorStorage.getDirector(id).orElseThrow(() ->
                new DirectorNotFoundException(String.format(
                        "Директор с ID %s не найден", id)));
    }

    @Override
    public Director createDirector(Director director) {
        return directorStorage.createDirector(director).orElseThrow(() ->
                new DirectorAlreadyExistsException(String.format(
                        "Директор с ID %s уже существует", director.getId())));
    }

    @Override
    public Director updateDirector(Director director) {
        return directorStorage.updateDirector(director).orElseThrow(() ->
                new DirectorNotFoundException(String.format(
                        "Директор с ID %s не найден", director.getId())));
    }

    @Override
    public void removeDirector(long id) {
        directorStorage.removeDirector(id);
    }
}
