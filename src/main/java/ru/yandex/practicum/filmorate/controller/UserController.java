package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    int id = 1;

    @GetMapping
    public List<User> getFilms() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    @ResponseBody
    public User addFilm(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            throw new ValidationException("Такой пользователь уже существует");
        } else {
            user.setId(id);
            checkNameUser(user);
            users.put(id, user);
            id++;
        }

        return user;
    }

    @PutMapping
    @ResponseBody
    public User updateFilm(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            throw new ValidationException("Такого пользователя не существует");
        }

        return user;
    }

    private void checkNameUser(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
    }
}
