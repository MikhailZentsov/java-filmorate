package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InMemoryUserService implements UserService {
    private final UserStorage userStorage;

    public InMemoryUserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUser(Long id) {
        return userStorage.getUser(id);
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getFriends(Long id) {
        User user = userStorage.getUser(id);
        return user.getFriends().stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public List<User> addFriend(Long idUser, Long idFriend) {
        User user = userStorage.getUser(idUser);
        User friend = userStorage.getUser(idFriend);
        user.addFriend(idFriend);
        friend.addFriend(idUser);

        return user.getFriends().stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public List<User> removeFriend(Long idUser, Long idFriend) {
        User user = userStorage.getUser(idUser);
        User friend = userStorage.getUser(idFriend);
        user.removeFriend(idFriend);
        friend.removeFriend(idUser);

        return user.getFriends().stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long idUser, Long idFriend) {
        User user = userStorage.getUser(idUser);
        User friend = userStorage.getUser(idFriend);

        if (user.getFriends() == null || friend.getFriends() == null) {
            return new ArrayList<>();
        }

        Set<Long> userFriends = user.getFriends();
        Set<Long> friendFriends = friend.getFriends();
        userFriends.retainAll(friendFriends);

        return userFriends.stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }
}
