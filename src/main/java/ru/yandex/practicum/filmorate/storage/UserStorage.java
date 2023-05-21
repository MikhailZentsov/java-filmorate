package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<List<User>> getUsers();

    Optional<User> getUser(Long id);

    Optional<User> addUser(User user);

    Optional<User> updateUser(User user);
}
