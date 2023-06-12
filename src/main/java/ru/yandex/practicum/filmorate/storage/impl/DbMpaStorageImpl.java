package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.mapper.Mapper;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class DbMpaStorageImpl implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public List<Mpa> findAll() {
        String sqlQuery = "select RATING_NAME from RATINGS";

        List<Mpa> mpas = jdbcTemplate.query(sqlQuery, Mapper::mapRowToMpa);
        log.info("Получены все рейтинги");

        return mpas;
    }

    @Override
    @Transactional
    public Optional<Mpa> getById(Integer id) {
        String sqlQuery = "select RATING_NAME from RATINGS where RATING_ID = ?";

        try {
            Optional<Mpa> mpa = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, Mapper::mapRowToMpa, id));
            log.info("Рейтинг с ID = {} получен.", id);

            return mpa;
        } catch (DataAccessException e) {
            log.info("Рейтинг с ID = {} не найден.", id);

            return Optional.empty();
        }
    }
}
