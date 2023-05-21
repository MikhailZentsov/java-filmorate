package ru.yandex.practicum.filmorate.service.impl.bd;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.service.impl.memory.InMemoryFilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Service("BdFilmService")
public class BdFilmService extends InMemoryFilmService {
    public BdFilmService(@Qualifier("BdFilmStorage") FilmStorage filmStorage,
                         @Qualifier("BdUserStorage") UserStorage userStorage) {
        super(filmStorage, userStorage);
    }
}
