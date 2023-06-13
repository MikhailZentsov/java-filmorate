package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.validator.UserLoginConstraint;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonIgnoreProperties(value = {"friends"})
@Getter
@Setter
public class User {

    private long id;

    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Email не корректный")
    private String email;

    @NotNull(message = "Логин не может быть пустым")
    @NotBlank(message = "Логин не может быть пустым")
    @UserLoginConstraint(message = "Логин не может содержать пробелы")
    private String login;

    private String name;

    @Past(message = "Дата рождения не может быть больше текущей даты")
    private LocalDate birthday;

    private User() {
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("USER_ID", id);
        values.put("EMAIL", email);
        values.put("LOGIN", login);
        values.put("USER_NAME", name);
        values.put("BIRTHDAY", birthday);

        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(login, user.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                '}';
    }

    public static class Builder {
        private final User newUser;

        public Builder() {
            newUser = new User();
        }

        public Builder id(Long id) {
            newUser.setId(id);
            return this;
        }

        public Builder name(String name) {
            newUser.setName(name);
            return this;
        }

        public Builder login(String login) {
            newUser.setLogin(login);
            return this;
        }

        public Builder email(String email) {
            newUser.setEmail(email);
            return this;
        }

        public Builder birthday(LocalDate birthday) {
            newUser.setBirthday(birthday);
            return this;
        }

        public User build() {
            return newUser;
        }
    }
}
