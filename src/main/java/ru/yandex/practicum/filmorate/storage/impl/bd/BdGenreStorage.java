package ru.yandex.practicum.filmorate.storage.impl.bd;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.impl.bd.mapper.Mapper;

import java.util.List;
import java.util.Optional;

@Repository("BdGenreStorage")
public class BdGenreStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public BdGenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<List<Genre>> getGenres() {
        String sqlQuery = "select GENRE_NAME from GENRES";

        return Optional.of(jdbcTemplate.query(sqlQuery, Mapper::mapRowToGenre));
    }

    @Override
    public Optional<Genre> getGenre(Integer id) {
        String sqlQuery = "select GENRE_NAME from GENRES where GENRE_ID = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, Mapper::mapRowToGenre, id));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }
}
