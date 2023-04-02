package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;

public class FilmDurationConstraintValidator implements ConstraintValidator<FilmDurationConstraint, Duration> {

    @Override
    public boolean isValid(Duration duration, ConstraintValidatorContext constraintValidatorContext) {
        return !duration.isZero();
    }
}
