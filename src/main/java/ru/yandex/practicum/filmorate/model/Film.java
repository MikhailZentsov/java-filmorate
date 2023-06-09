package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.validator.FilmReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
public class Film {

    private long id;

    @NotNull(message = "Название не может быть пустым")
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания 200 символов")
    private String description;

    @FilmReleaseDateConstraint(message = "Дата не может быть раньше создания первого фильма в истории")
    private LocalDate releaseDate;

    @Positive(message = "Длительность не может быть отрицательной или нулевой")
    private Integer duration;

    private Mpa mpa;

    private Set<Genre> genres;

    private Set<Director> directors;

    public Film(long id, String name, String description, LocalDate releaseDate, Integer duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        genres = new LinkedHashSet<>();
        directors = new LinkedHashSet<>();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("FILM_ID", id);
        values.put("FILM_NAME", name);
        values.put("FILM_DESCRIPTION", description);
        values.put("RATING_ID", mpa.getId());
        values.put("RELEASE_DATE", releaseDate);
        values.put("DURATION", duration);

        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return Objects.equals(name, film.name) && Objects.equals(description, film.description) &&
                Objects.equals(releaseDate, film.releaseDate) && Objects.equals(duration, film.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, releaseDate, duration);
    }

    @Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", releaseDate=" + releaseDate +
                ", duration=" + duration +
                ", mpa=" + mpa +
                '}';
    }
}
