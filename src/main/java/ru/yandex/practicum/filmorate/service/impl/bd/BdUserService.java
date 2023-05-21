package ru.yandex.practicum.filmorate.service.impl.bd;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.service.impl.memory.InMemoryUserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Service("BdUserService")
public class BdUserService extends InMemoryUserService {
    public BdUserService(@Qualifier("BdUserStorage") UserStorage userStorage) {
        super(userStorage);
    }
}
