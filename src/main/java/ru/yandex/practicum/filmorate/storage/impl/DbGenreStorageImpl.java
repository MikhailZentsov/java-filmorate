package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mapper.Mapper;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DbGenreStorageImpl implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public List<Genre> findAll() {
        String sqlQuery = "select GENRE_NAME from GENRES";

        return jdbcTemplate.query(sqlQuery, Mapper::mapRowToGenre);
    }

    @Override
    @Transactional
    public Optional<Genre> getById(Integer id) {
        String sqlQuery = "select GENRE_NAME from GENRES where GENRE_ID = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, Mapper::mapRowToGenre, id));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }
}
