package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.mapper.Mapper;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DbDirectorStorageImpl implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<List<Director>> getDirectors() {
        String sqlQueryGetUsers = "select DIRECTOR_ID as id, " +
                "DIRECTOR_NAME as name " +
                "from DIRECTORS";

        List<Director> directors = jdbcTemplate.query(sqlQueryGetUsers, Mapper::mapToRowDirector);

        return Optional.of(directors);
    }

    @Override
    public Optional<Director> getDirector(long id) {
        String sqlQueryGetDirector = "select DIRECTOR_ID as id, " +
                " DIRECTOR_NAME as name " +
                "from DIRECTORS " +
                "where DIRECTOR_ID = ?";

        Director director;

        try {
            director = jdbcTemplate.queryForObject(sqlQueryGetDirector, Mapper::mapToRowDirector, id);
        } catch (DataAccessException e) {
            return Optional.empty();
        }

        assert director != null;

        return Optional.of(director);
    }

    @Override
    public Optional<Director> createDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("DIRECTORS")
                .usingGeneratedKeyColumns("DIRECTOR_ID");

        long directorId = simpleJdbcInsert.executeAndReturnKey(director.toMap()).longValue();
        return getDirector(directorId);
    }

    @Override
    public Optional<Director> updateDirector(Director director) {
        String sqlQueryUpdateDirector = "update DIRECTORS set DIRECTOR_NAME = ? where DIRECTOR_ID = ?";

        try {
            jdbcTemplate.update(sqlQueryUpdateDirector,
                    director.getName(),
                    director.getId()
            );
        } catch (DataAccessException e) {
            return Optional.empty();
        }

        return getDirector(director.getId());
    }

    @Override
    public void removeDirector(long id) {
        String sqlQueryRemoveDirector = "delete from DIRECTORS " +
                "where DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlQueryRemoveDirector, id);
    }
}
