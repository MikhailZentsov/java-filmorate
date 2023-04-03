package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    @ResponseBody
    public User addUser(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            log.warn("Такой пользователь уже существует");
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Такой пользователь уже существует");
        } else {
            user.setId(id);
            normalizeNameUser(user);
            users.put(id, user);
            id++;
            log.info("Пользователь {} добавлен", user);
        }
        return user;
    }

    @PutMapping
    @ResponseBody
    public User updateUser(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            normalizeNameUser(user);
            users.put(user.getId(), user);
            log.info("Пользователь {} добавлен", user);
        } else {
            log.warn("Такого пользователя не существует");
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Такого пользователя не существует");
        }
        return user;
    }

    private void normalizeNameUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
