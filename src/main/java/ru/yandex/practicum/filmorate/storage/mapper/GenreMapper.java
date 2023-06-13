package ru.yandex.practicum.filmorate.storage.mapper;

import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreMapper {

    public static Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.valueOf(resultSet.getString("GENRE_NAME"));
    }
}
