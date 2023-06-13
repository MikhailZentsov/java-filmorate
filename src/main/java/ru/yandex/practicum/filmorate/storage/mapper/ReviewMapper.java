package ru.yandex.practicum.filmorate.storage.mapper;

import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReviewMapper {

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
