package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
public class Director {

    private long id;

    @NotBlank(message = "Имя режиссера не может быть пустым")
    private String name;

    private Director() {

    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("DIRECTOR_ID", id);
        values.put("DIRECTOR_NAME", name);
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Director director = (Director) o;
        return id == director.id && Objects.equals(name, director.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Director{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public static class Builder {
        private final Director director;

        public Builder() {
            director = new Director();
        }

        public Builder id(long id) {
            director.setId(id);
            return this;
        }

        public Builder name(String name) {
            director.setName(name);
            return this;
        }

        public Director build() {
            return director;
        }
    }
}
