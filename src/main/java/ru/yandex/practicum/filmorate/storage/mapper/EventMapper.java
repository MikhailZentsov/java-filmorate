package ru.yandex.practicum.filmorate.storage.mapper;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventMapper {

    public static Event mapRowToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        return new Event.Builder()
                .eventId(resultSet.getLong("EVENT_ID"))
                .entityId(resultSet.getLong("ENTITY_ID"))
                .userId(resultSet.getLong("USER_ID"))
                .timestamp(resultSet.getLong("EVENT_TIMESTAMP"))
                .eventType(EventType.valueOf(resultSet.getString("EVENT_TYPE")))
                .eventOperation(EventOperation.valueOf(resultSet.getString("EVENT_OPERATION")))
                .build();
    }
}
