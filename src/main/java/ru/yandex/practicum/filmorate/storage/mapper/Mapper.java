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
        return new Film(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getDate("releaseDate").toLocalDate(),
                resultSet.getInt("duration"),
                Mpa.valueOf(resultSet.getString("mpa"))
        );
    }

    public static User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return new User(
                resultSet.getLong("id"),
                resultSet.getString("login"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getDate("birthday").toLocalDate()
        );
    }

    public static Event mapRowToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        Event event = new Event();
        event.setEventId(resultSet.getLong("EVENT_ID"));
        event.setEntityId(resultSet.getLong("ENTITY_ID"));
        event.setUserId(resultSet.getLong("USER_ID"));
        event.setTimestamp(resultSet.getLong("EVENT_TIMESTAMP"));
        event.setEventType(EventType.valueOf(resultSet.getString("EVENT_TYPE")));
        event.setOperation(EventOperation.valueOf(resultSet.getString("EVENT_OPERATION")));

        return event;
    }

    public static Director mapToRowDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return new Director(
                resultSet.getLong("id"),
                resultSet.getString("name")
        );
    }

    public static Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        Review review = new Review();
        review.setReviewId(resultSet.getLong("REVIEW_ID"));
        review.setContent(resultSet.getString("CONTENT"));
        review.setIsPositive(resultSet.getBoolean("IS_POSITIVE"));
        review.setUserId(resultSet.getLong("USER_ID"));
        review.setFilmId(resultSet.getLong("FILM_ID"));
        review.setUseful(resultSet.getLong("USEFUL"));

        return review;
    }
}
