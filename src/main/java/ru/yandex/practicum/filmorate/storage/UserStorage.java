package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> findAll();

    Optional<User> getById(Long id);

    Optional<User> saveOne(User user);

    Optional<User> updateOne(User user);

    List<User> findAllFriendsById(Long id);

    List<User> saveOneFriend(Long idUser, Long idFriend);

    List<User> deleteOneFriend(Long idUser, Long idFriend);

    List<Film> findRecommendationsFilms(Long userId);

    Optional<Boolean> deleteUserById(long userId);
}
