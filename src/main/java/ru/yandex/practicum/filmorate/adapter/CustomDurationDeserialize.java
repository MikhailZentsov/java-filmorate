package ru.yandex.practicum.filmorate.adapter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Duration;

public class CustomDurationDeserialize extends StdDeserializer<Duration> {

    public CustomDurationDeserialize() {
        this(null);
    }

    public CustomDurationDeserialize(Class<Duration> duration) {
        super(duration);
    }

    @Override
    public Duration deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        if (jsonParser.getLongValue() < 0) {
            return Duration.ZERO;
        }
        return Duration.ofMinutes(jsonParser.getLongValue());
    }
}
