package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DbUserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public DbUserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        return userStorage.findAll().orElse(new ArrayList<>());
    }

    public User getUser(Long id) {
        return userStorage.getById(id).orElseThrow(() ->
                new UserNotFoundException(String.format(
                    "Пользователь с ID %s не найден", id)));
    }

    public User addUser(User user) {
        return userStorage.saveOne(normalizeNameUser(user)).orElseThrow(() ->
                new UserAlreadyExistsException(String.format(
                    "Пользователь с ID %s уже существует", user.getId())));
    }

    public User updateUser(User user) {
        return userStorage.updateOne(normalizeNameUser(user)).orElseThrow(() ->
                new UserNotFoundException(String.format(
                    "Пользователь с ID %s не найден", user.getId())));
    }

    public List<User> getFriends(Long id) {
        userStorage.getById(id).orElseThrow(() ->
                new UserNotFoundException(String.format(
                    "Пользователь с ID %s не найден", id)));

        return userStorage.findAllFriendsById(id).orElse(new ArrayList<>());
    }

    public List<User> addFriend(Long idUser, Long idFriend) {
        userStorage.getById(idUser).orElseThrow(() ->
                new UserNotFoundException(String.format(
                    "Пользователь с ID %s не найден", idUser)));
         userStorage.getById(idFriend).orElseThrow(() ->
                new UserNotFoundException(String.format(
                    "Пользователь с ID %s не найден", idFriend)));

        return userStorage.saveOneFriend(idUser, idFriend).orElse(new ArrayList<>());
    }

    public List<User> removeFriend(Long idUser, Long idFriend) {
        userStorage.getById(idUser).orElseThrow(() ->
                new UserNotFoundException(String.format(
                    "Пользователь с ID %s не найден", idUser)));
        userStorage.getById(idFriend).orElseThrow(() ->
                new UserNotFoundException(String.format(
                    "Пользователь с ID %s не найден", idFriend)));

        return userStorage.deleteOneFriend(idUser, idFriend).orElse(new ArrayList<>());
    }

    public List<User> getCommonFriends(Long idUser, Long idFriend) {
        userStorage.getById(idUser).orElseThrow(() ->
                new UserNotFoundException(String.format(
                    "Пользователь с ID %s не найден", idUser)));
        userStorage.getById(idFriend).orElseThrow(() ->
                new UserNotFoundException(String.format(
                    "Пользователь с ID %s не найден", idFriend)));

        List<User> userFriends = userStorage.findAllFriendsById(idUser).orElse(new ArrayList<>());

        List<User> friendFriends = userStorage.findAllFriendsById(idFriend).orElse(new ArrayList<>());

        if (userFriends.isEmpty() || friendFriends.isEmpty()) {
            return new ArrayList<>();
        }

        userFriends.retainAll(friendFriends);

        return userFriends.stream()
                .sorted(Comparator.comparingLong(User::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getRecommendations(Long id) {
        userStorage.getById(id).orElseThrow(() ->
                new UserNotFoundException(String.format(
                        "Пользователь с ID %s не найден", id)));

        return userStorage.findRecommendationsFilms(id);
    }

    private User normalizeNameUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return user;
    }
}
