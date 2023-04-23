package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new UserNotFoundException(String.format(
                    "Пользователь с ID %s не найден",
                    id
            ));
        }
    }

    @Override
    public User addUser(User user) {
        if (users.containsKey(user.getId())) {
            throw new UserAlreadyExistsException(String.format(
                    "Пользователь с ID %s уже существует",
                    user.getId()
            ));
        } else {
            user.setId(id);
            normalizeNameUser(user);
            users.put(user.getId(), user);
            id++;
            return user;
        }
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            normalizeNameUser(user);
            users.put(user.getId(), user);
            return user;
        } else {
            throw new UserNotFoundException(String.format(
                    "Пользователь с ID %s не найден",
                    user.getId()
            ));
        }
    }

    private void normalizeNameUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
