package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<List<User>> findAll();

    Optional<User> getById(Long id);

    Optional<User> saveOne(User user);

    Optional<User> updateOne(User user);

    Optional<List<User>> findAllFriendsById(Long id);

    Optional<List<User>> saveOneFriend(Long idUser, Long idFriend);

    Optional<List<User>> deleteOneFriend(Long idUser, Long idFriend);

    List<Film> findRecommendationsFilms(Long userId);

    Optional<Boolean> deleteUserById(long userId);
}
