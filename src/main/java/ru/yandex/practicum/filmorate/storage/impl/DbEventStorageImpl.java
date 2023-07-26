package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.mapper.EventMapper;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class DbEventStorageImpl implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void saveOne(Event event) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("EVENTS")
                .usingGeneratedKeyColumns("EVENT_ID");

        Long eventId = simpleJdbcInsert.executeAndReturnKey(event.toMap()).longValue();
        log.info("Создано событие c ID = {} от пользователя с ID = {}",
                eventId,
                event.getUserId());
    }

    @Override
    @Transactional
    public List<Event> findAllById(Long idUser) {
        String sqlQueryFindAllById = "select EVENT_ID, " +
                "       USER_ID, " +
                "       ENTITY_ID, " +
                "       EVENT_TIMESTAMP, " +
                "       EVENT_TYPE, " +
                "       EVENT_OPERATION " +
                "from EVENTS " +
                "where USER_ID = ? " +
                "order by event_timestamp ";

        List<Event> events = jdbcTemplate.query(sqlQueryFindAllById, EventMapper::mapRowToEvent, idUser);
        log.info("Получен список событий пользователя с ID = {}", idUser);
        return events;
    }
}
