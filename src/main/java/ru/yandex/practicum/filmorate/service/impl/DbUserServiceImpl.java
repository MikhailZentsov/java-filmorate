package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DbUserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final EventService eventService;

    public DbUserServiceImpl(UserStorage userStorage, EventService eventService) {
        this.userStorage = userStorage;
        this.eventService = eventService;
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

        List<User> friends = userStorage.saveOneFriend(idUser, idFriend).orElse(new ArrayList<>());
        eventService.createAddFriend(idUser, idFriend);

        return friends;
    }

    public List<User> removeFriend(Long idUser, Long idFriend) {
        userStorage.getById(idUser).orElseThrow(() ->
                new UserNotFoundException(String.format(
                        "Пользователь с ID %s не найден", idUser)));
        userStorage.getById(idFriend).orElseThrow(() ->
                new UserNotFoundException(String.format(
                        "Пользователь с ID %s не найден", idFriend)));

        List<User> friends = userStorage.deleteOneFriend(idUser, idFriend).orElse(new ArrayList<>());
        eventService.createRemoveFriend(idUser, idFriend);

        return friends;
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
    public List<Event> getFeed(Long id) {
        return eventService.findEventsByUserId(id);
    }

    @Override
    public List<Film> getRecommendations(Long userId) {
        userStorage.getById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format(
                        "Пользователь с ID %s не найден", userId)));

        return userStorage.findRecommendationsFilms(userId);
    }

    private User normalizeNameUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return user;
    }
}
