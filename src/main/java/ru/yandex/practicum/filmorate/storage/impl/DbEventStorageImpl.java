package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.mapper.Mapper;

import java.util.List;

@Repository
public class DbEventStorageImpl implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    public DbEventStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void saveOne(Event event) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("EVENTS")
                .usingGeneratedKeyColumns("EVENT_ID");

        simpleJdbcInsert.execute(event.toMap());
    }

    @Override
    @Transactional
    public List<Event> findAllById(Long idUser) {
        String sqlQueryFindAllById = "select event_id, " +
                "       user_id, " +
                "       entity_id, " +
                "       event_timestamp, " +
                "       event_type, " +
                "       event_operation " +
                "from EVENTS " +
                "where USER_ID = ?";

        return jdbcTemplate.query(sqlQueryFindAllById, Mapper::mapRowToEvent, idUser);
    }
}
