package ru.yandex.practicum.filmorate.storage.mapper;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmMapper {

    public static Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return new Film.Builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("releaseDate").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(Mpa.valueOf(resultSet.getString("mpa")))
                .rate(resultSet.getDouble("rate"))
                .build();
    }
}
