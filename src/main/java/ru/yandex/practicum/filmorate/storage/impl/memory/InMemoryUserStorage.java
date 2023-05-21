package ru.yandex.practicum.filmorate.storage.impl.memory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Repository("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public Optional<List<User>> getUsers() {
        return Optional.of(new ArrayList<>(users.values()));
    }

    @Override
    public Optional<User> getUser(Long id) {
        if (users.containsKey(id)) {
            return Optional.of(users.get(id));
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> addUser(User user) {
        if (!users.containsKey(user.getId())) {
            user.setId(id);
            users.put(user.getId(), user);
            id++;
            return Optional.of(user);
        }

         return Optional.empty();
    }

    @Override
    public Optional<User> updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return Optional.of(user);
        }

        return Optional.empty();
    }
}
