package ru.yandex.practicum.filmorate.adapter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Duration;

@JsonSerialize
public class CustomDurationSerialize extends StdSerializer<Duration> {

    public CustomDurationSerialize() {
        this(null);
    }

    public CustomDurationSerialize(Class<Duration> duration) {
        super(duration);
    }

    @Override
    public void serialize(Duration duration, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(duration.toMinutes());
    }
}
