package ru.yandex.practicum.filmorate.storage.mapper;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MpaMapper {

    public static Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.valueOf(resultSet.getString("RATING_NAME"));
    }
}
