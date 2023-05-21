package ru.yandex.practicum.filmorate.service.impl.memory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service("InMemoryUserService")
public class InMemoryUserService implements UserService {
    private final UserStorage userStorage;

    public InMemoryUserService(@Qualifier("InMemoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        return userStorage.getUsers().orElse(new ArrayList<>());
    }

    public User getUser(Long id) {
        return userStorage.getUser(id).orElseThrow(() ->
                new UserNotFoundException(String.format(
                    "Пользователь с ID %s не найден", id
        )));
    }

    public User addUser(User user) {
        return userStorage.addUser(normalizeNameUser(user)).orElseThrow(() ->
                new UserAlreadyExistsException(String.format(
                    "Пользователь с ID %s уже существует", user.getId()
        )));
    }

    public User updateUser(User user) {
        return userStorage.updateUser(normalizeNameUser(user)).orElseThrow(() ->
                new UserNotFoundException(String.format(
                    "Пользователь с ID %s не найден", user.getId()
        )));
    }

    public List<User> getFriends(Long id) {
        User user = userStorage.getUser(id).orElseThrow(() ->
                new UserNotFoundException(String.format(
                    "Пользователь с ID %s не найден", id
        )));

        return user.getFriends().stream()
                .map(userStorage::getUser)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<User> addFriend(Long idUser, Long idFriend) {
        User user = userStorage.getUser(idUser).orElseThrow(() ->
                new UserNotFoundException(String.format(
                    "Пользователь с ID %s не найден", idUser
        )));
        User friend = userStorage.getUser(idFriend).orElseThrow(() ->
                new UserNotFoundException(String.format(
                    "Пользователь с ID %s не найден", idFriend
        )));
        user.getFriends().add(friend.getId());
        userStorage.updateUser(user);
        userStorage.updateUser(friend);

        return user.getFriends().stream()
                .map(userStorage::getUser)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<User> removeFriend(Long idUser, Long idFriend) {
        User user = userStorage.getUser(idUser).orElseThrow(() ->
                new UserNotFoundException(String.format(
                    "Пользователь с ID %s не найден", idUser
        )));
        User friend = userStorage.getUser(idFriend).orElseThrow(() ->
                new UserNotFoundException(String.format(
                    "Пользователь с ID %s не найден", idFriend
        )));
        user.getFriends().remove(friend.getId());
        userStorage.updateUser(user);
        userStorage.updateUser(friend);

        return user.getFriends().stream()
                .map(userStorage::getUser)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long idUser, Long idFriend) {
        User user = userStorage.getUser(idUser).orElseThrow(() ->
                new UserNotFoundException(String.format(
                    "Пользователь с ID %s не найден", idUser
        )));
        User friend = userStorage.getUser(idFriend).orElseThrow(() ->
                new UserNotFoundException(String.format(
                    "Пользователь с ID %s не найден", idFriend
        )));

        Set<User> userFriends = user.getFriends().stream()
                .map(userStorage::getUser)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        Set<User> friendFriends = friend.getFriends().stream()
                .map(userStorage::getUser)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        if (userFriends.isEmpty() || friendFriends.isEmpty()) {
            return new ArrayList<>();
        }

        userFriends.retainAll(friendFriends);

        return userFriends.stream()
                .sorted(Comparator.comparingLong(User::getId))
                .collect(Collectors.toList());
    }

    private User normalizeNameUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return user;
    }
}
