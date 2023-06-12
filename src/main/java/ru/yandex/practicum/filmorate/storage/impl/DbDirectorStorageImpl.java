package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.mapper.DirectorMapper;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class DbDirectorStorageImpl implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public List<Director> getDirectors() {
        String sqlQueryGetUsers = "select DIRECTOR_ID as id, " +
                "DIRECTOR_NAME as name " +
                "from DIRECTORS";

        List<Director> directors = jdbcTemplate.query(sqlQueryGetUsers, DirectorMapper::mapToRowDirector);
        log.info("Получен список деректоров из {} элементов.", directors.size());

        return directors;
    }

    @Override
    @Transactional
    public Optional<Director> getDirector(long id) {
        String sqlQueryGetDirector = "select DIRECTOR_ID as id, " +
                " DIRECTOR_NAME as name " +
                "from DIRECTORS " +
                "where DIRECTOR_ID = ?";

        Director director;

        try {
            director = jdbcTemplate.queryForObject(sqlQueryGetDirector, DirectorMapper::mapToRowDirector, id);
        } catch (DataAccessException e) {
            log.info("Директор с ID = {} не найден.", id);
            return Optional.empty();
        }

        log.info("Директор с ID = {} найден.", id);

        return Optional.of(director);
    }

    @Override
    @Transactional
    public Optional<Director> createDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("DIRECTORS")
                .usingGeneratedKeyColumns("DIRECTOR_ID");

        long directorId = simpleJdbcInsert.executeAndReturnKey(director.toMap()).longValue();

        director.setId(directorId);
        log.info("Директор с ID = {} создан.", directorId);

        return Optional.of(director);
    }

    @Override
    @Transactional
    public Optional<Director> updateDirector(Director director) {
        String sqlQueryUpdateDirector = "update DIRECTORS set DIRECTOR_NAME = ? where DIRECTOR_ID = ?";

        if (jdbcTemplate.update(sqlQueryUpdateDirector, director.getName(), director.getId()) == 0) {
            log.info("Директор с ID = {} не найден.", director.getId());
            return Optional.empty();
        }

        log.info("Директор с ID = {} обновлен.", director.getId());

        return Optional.of(director);
    }

    @Override
    @Transactional
    public void removeDirector(long id) {
        String sqlQueryRemoveDirector = "delete from DIRECTORS where DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlQueryRemoveDirector, id);
        log.info("Директор с ID = {} удален.", id);
    }
}
