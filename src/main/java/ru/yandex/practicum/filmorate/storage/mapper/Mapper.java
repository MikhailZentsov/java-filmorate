package ru.yandex.practicum.filmorate.storage.mapper;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Mapper {

    public static Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.valueOf(resultSet.getString("GENRE_NAME"));
    }

    public static Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.valueOf(resultSet.getString("RATING_NAME"));
    }

    public static Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return new Film.Builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("releaseDate").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(Mpa.valueOf(resultSet.getString("mpa")))
                .build();
    }

    public static User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return new User.Builder()
                .id(resultSet.getLong("id"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .email(resultSet.getString("email"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

    public static Event mapRowToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        return new Event.Builder()
                .eventId(resultSet.getLong("EVENT_ID"))
                .entityId(resultSet.getLong("ENTITY_ID"))
                .userId(resultSet.getLong("USER_ID"))
                .timestamp(resultSet.getLong("EVENT_TIMESTAMP"))
                .eventType(EventType.valueOf(resultSet.getString("EVENT_TYPE")))
                .eventOperation(EventOperation.valueOf(resultSet.getString("EVENT_OPERATION")))
                .build();
    }

    public static Director mapToRowDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return new Director.Builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }

    public static Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        return new Review.Builder()
                .reviewId(resultSet.getLong("REVIEW_ID"))
                .content(resultSet.getString("CONTENT"))
                .isPositive(resultSet.getBoolean("IS_POSITIVE"))
                .userId(resultSet.getLong("USER_ID"))
                .filmId(resultSet.getLong("FILM_ID"))
                .useful(resultSet.getLong("USEFUL"))
                .build();
    }
}
