package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.adapter.CustomDurationDeserialize;
import ru.yandex.practicum.filmorate.adapter.CustomDurationSerialize;
import ru.yandex.practicum.filmorate.validator.FilmDurationConstraint;
import ru.yandex.practicum.filmorate.validator.FilmReleaseDateConstraint;

import java.time.Duration;
import java.time.LocalDate;


@Data
@Builder
public class Film {

    @EqualsAndHashCode.Exclude
    private int id;

    @NotNull(message = "Название не может быть пустым")
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания 200 символов")
    private String description;

    @FilmReleaseDateConstraint(message = "Дата не может быть раньше создания первого фильма в истории")
    private LocalDate releaseDate;

    @JsonSerialize(using = CustomDurationSerialize.class)
    @JsonDeserialize(using = CustomDurationDeserialize.class)
    @FilmDurationConstraint(message = "Длительность не может быть отрицательной или нулевой")
    private Duration duration;
}
