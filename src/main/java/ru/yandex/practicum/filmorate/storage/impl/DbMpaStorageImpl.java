package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.mapper.Mapper;

import java.util.List;
import java.util.Optional;

@Repository
public class DbMpaStorageImpl implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public DbMpaStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<List<Mpa>> findAll() {
        String sqlQuery = "select RATING_NAME from RATINGS";

        return Optional.of(jdbcTemplate.query(sqlQuery, Mapper::mapRowToMpa));
    }

    @Override
    public Optional<Mpa> getById(Integer id) {
        String sqlQuery = "select RATING_NAME from RATINGS where RATING_ID = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, Mapper::mapRowToMpa, id));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }
}
