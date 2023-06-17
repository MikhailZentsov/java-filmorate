package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.validator.FilmReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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

    private Double rate;

    private Set<Genre> genres;

    private Set<Director> directors;

    private Film() {
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
        return Objects.equals(name, film.name)
                && Objects.equals(description, film.description)
                && Objects.equals(releaseDate, film.releaseDate)
                && Objects.equals(duration, film.duration);
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

    public static class Builder {
        private final Film newFilm;

        public Builder() {
            newFilm = new Film();
        }

        public Builder id(Long id) {
            newFilm.setId(id);
            return this;
        }

        public Builder name(String name) {
            newFilm.setName(name);
            return this;
        }

        public Builder description(String description) {
            newFilm.setDescription(description);
            return this;
        }

        public Builder releaseDate(LocalDate releaseDate) {
            newFilm.setReleaseDate(releaseDate);
            return this;
        }

        public Builder duration(int duration) {
            newFilm.setDuration(duration);
            return this;
        }

        public Builder mpa(Mpa mpa) {
            newFilm.setMpa(mpa);
            return this;
        }

        public Builder genres(Set<Genre> genres) {
            newFilm.setGenres(Objects.requireNonNullElseGet(genres, LinkedHashSet::new));
            return this;
        }

        public Builder directors(Set<Director> directors) {
            newFilm.setDirectors(Objects.requireNonNullElseGet(directors, LinkedHashSet::new));
            return this;
        }

        public Builder rate(Double rate) {
            newFilm.setRate(rate);
            return this;
        }

        public Film build() {
            return newFilm;
        }
    }
}
