package ru.yandex.practicum.filmorate.storage.impl.bd;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.impl.bd.mapper.Mapper;

import java.util.List;
import java.util.Optional;

@Repository("BdMpaStorage")
public class BdMpaStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public BdMpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<List<Mpa>> getMpas() {
        String sqlQuery = "select RATING_NAME from RATINGS";

        return Optional.of(jdbcTemplate.query(sqlQuery, Mapper::mapRowToMpa));
    }

    @Override
    public Optional<Mpa> getMpa(Integer id) {
        String sqlQuery = "select RATING_NAME from RATINGS where RATING_ID = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, Mapper::mapRowToMpa, id));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }
}
