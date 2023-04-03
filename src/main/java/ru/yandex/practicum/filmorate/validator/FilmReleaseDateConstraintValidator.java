package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class FilmReleaseDateConstraintValidator implements ConstraintValidator<FilmReleaseDateConstraint, LocalDate> {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12,28);

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext constraintValidatorContext) {
        return releaseDate == null || !releaseDate.isBefore(MIN_RELEASE_DATE);
    }
}
