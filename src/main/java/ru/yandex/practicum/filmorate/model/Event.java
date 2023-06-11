package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Event {

    private Long timestamp;

    private Long eventId;

    private Long userId;

    private Long entityId;

    private EventType eventType;

    private EventOperation operation;

    public Event() {

    }

    public Map<String, Object> toMap() {
        Map<String, Object> mapEvent = new HashMap<>();
        mapEvent.put("EVENT_ID", eventId);
        mapEvent.put("USER_ID", userId);
        mapEvent.put("ENTITY_ID", entityId);
        mapEvent.put("EVENT_TIMESTAMP", timestamp);
        mapEvent.put("EVENT_TYPE", eventType.getName());
        mapEvent.put("EVENT_OPERATION", operation.getName());

        return mapEvent;
    }

    public static class Builder {
        private final Event newEvent;

        public Builder() {
            newEvent = new Event();
        }

        public Builder eventId(Long eventId) {
            newEvent.setEventId(eventId);
            return this;
        }

        public Builder userId(Long userId) {
            newEvent.setUserId(userId);
            return this;
        }

        public Builder entityId(Long entityId) {
            newEvent.setEntityId(entityId);
            return this;
        }

        public Builder eventType(EventType eventType) {
            newEvent.setEventType(eventType);
            return this;
        }

        public Builder eventOperation(EventOperation eventOperation) {
            newEvent.setOperation(eventOperation);
            return this;
        }

        public Event build() {
            newEvent.setTimestamp(Instant.now().toEpochMilli());
            return newEvent;
        }
    }
}
