package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private static UserController userController;
    private static User user;
    private Validator validator;

    @BeforeEach
    void setUp() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
        user = new User(
                1
                ,"dolore"
                ,"Nick Name"
                ,"mail@mail.ru"
                ,LocalDate.of(1946, 8, 20)
        );
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldAddCorrectUser() {
        userController.addUser(user);
        List<User> listUser = userController.getUsers();

        assertEquals(1, listUser.size(),
                "Количество пользователей не совпадает");

        User userFromMap = listUser.get(0);

        assertEquals(user, userFromMap,
                "Пользователь добавлен с ошибками");
    }

    @Test
    void shouldNotAddWithIncorrectEmailUser() {
        user.setEmail(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(0, violations.size(),
                "Количество сообщений об ошибке отличается от корректного с email null");

        user.setEmail("");
        violations = validator.validate(user);
        assertEquals(1, violations.size(),
                "Количество сообщений об ошибке отличается от корректного с ошибочным email");
        assertEquals(violations.iterator().next().getMessage(), "Email не корректный");

        user.setEmail("@");
        violations = validator.validate(user);
        assertEquals(1, violations.size(),
                "Количество сообщений об ошибке отличается от корректного с ошибочным email");
        assertEquals(violations.iterator().next().getMessage(), "Email не корректный");

        user.setEmail("aa@.ru");
        violations = validator.validate(user);
        assertEquals(1, violations.size(),
                "Количество сообщений об ошибке отличается от корректного с ошибочным email");
        assertEquals(violations.iterator().next().getMessage(), "Email не корректный");

        user.setEmail("zzzzzzz@zzzzz");
        violations = validator.validate(user);
        assertEquals(1, violations.size(),
                "Количество сообщений об ошибке отличается от корректного с ошибочным email");
        assertEquals(violations.iterator().next().getMessage(), "Email не корректный");

        user.setEmail("zzzzzzz@zzzzz.1234567");
        violations = validator.validate(user);
        assertEquals(1, violations.size(),
                "Количество сообщений об ошибке отличается от корректного с ошибочным email");
        assertEquals(violations.iterator().next().getMessage(), "Email не корректный");

        user.setEmail("ya@ya.ru");
        violations = validator.validate(user);
        assertEquals(0, violations.size(),
                "Количество сообщений об ошибке отличается от корректного с верным email");
    }

    @Test
    void shouldNotAddWithEmptyLoginUser() {
        user.setLogin(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(2, violations.size(),
                "Количество сообщений об ошибке отличается от корректного с логином null");
        assertEquals(violations.iterator().next().getMessage(), "Логин не может быть пустым");

        user.setLogin("");
        violations = validator.validate(user);
        violations.iterator().next();

        assertEquals(1, violations.size(),
                "Количество сообщений об ошибке отличается от корректного с пустым логином");
        assertEquals(violations.iterator().next().getMessage(), "Логин не может быть пустым");

        user.setLogin("   ");
        violations = validator.validate(user);

        assertEquals(2, violations.size(),
                "Количество сообщений об ошибке отличается от корректного с пустым логином");
        assertTrue(violations.stream().anyMatch(t -> t.getMessage().equals("Логин не может быть пустым")));

        user.setLogin("olo nlo");
        violations = validator.validate(user);

        assertEquals(1, violations.size(),
                "Количество сообщений об ошибке отличается от корректного с логином, содержащим пробелы");
        assertEquals(violations.iterator().next().getMessage(), "Логин не может содержать пробелы");
    }

    @Test
    void shouldChangeNameUserToLogin() {
        user.setName(null);
        userController.addUser(user);
        User userFromMap = userController.getUsers().get(0);

        assertEquals(user.getLogin(), userFromMap.getName(),
                "Имя не изменено");

        user.setName("");
        userController.updateUser(user);
        userFromMap = userController.getUsers().get(0);

        assertEquals(user.getLogin(), userFromMap.getName(),
                "Имя не изменено");

        user.setName("   ");
        userController.updateUser(user);
        userFromMap = userController.getUsers().get(0);

        assertEquals(user.getLogin(), userFromMap.getName(),
                "Имя не изменено");
    }

    @Test
    void shouldNotValidFutureBirthdayUser() {
        user.setBirthday(LocalDate.now().plusYears(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size(),
                "Количество сообщений об ошибке отличается от корректного с некорректной датой рождения");
        assertEquals(violations.iterator().next().getMessage(), "Дата рождения не может быть больше текущей даты");

        user.setBirthday(LocalDate.now().minusYears(1));
        violations = validator.validate(user);
        assertEquals(0, violations.size(),
                "Количество сообщений об ошибке отличается от корректного с корректной датой рождения");
    }
}