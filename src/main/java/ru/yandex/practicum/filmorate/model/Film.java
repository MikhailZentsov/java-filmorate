package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.validator.FilmReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
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

    @Positive(message = "Длительность не может быть отрицательной или нулевой")
    private Integer duration;
}
