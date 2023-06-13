package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class DbGenreStorageImpl implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public List<Genre> findAll() {
        String sqlQuery = "select GENRE_NAME from GENRES";

        List<Genre> genres = jdbcTemplate.query(sqlQuery, GenreMapper::mapRowToGenre);

        log.info("Получены все жанры");

        return genres;
    }

    @Override
    @Transactional
    public Optional<Genre> getById(Integer id) {
        String sqlQuery = "select GENRE_NAME from GENRES where GENRE_ID = ?";

        try {
            Optional<Genre> genre = Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery,
                    GenreMapper::mapRowToGenre,
                    id));
            log.info("Жанр с ID = {} получен.", id);
            return genre;
        } catch (DataAccessException e) {
            log.info("Жанр с ID = {} не найден.", id);
            return Optional.empty();
        }
    }
}
