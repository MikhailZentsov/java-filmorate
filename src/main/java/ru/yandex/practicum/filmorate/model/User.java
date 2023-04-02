package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.validator.UserLoginConstraint;

import java.time.LocalDate;

@Data
public class User {
    @EqualsAndHashCode.Exclude
    private int id;

    @Email(message = "Email не корректный")
    private String email;

    @NotNull(message = "Логин не должен быть пустым")
    @NotBlank(message = "Логин не должен быть пустым")
    @UserLoginConstraint(message = "Логин не должен содержать пробелы")
    private String login;

    private String name;

    @Past
    private LocalDate birthday;
}
