package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    List<User> getUsers();

    User getUser(Long id);

    User addUser(User user);

    User updateUser(User user);

    List<User> getFriends(Long id);

    List<User> addFriend(Long idUser, Long idFriend);

    List<User> removeFriend(Long idUser, Long idFriend);

    List<User> getCommonFriends(Long idUser, Long idFriend);

    List<Film> getRecommendations(Long userId);
}
