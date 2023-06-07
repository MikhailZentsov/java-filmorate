package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;

import javax.validation.ValidationException;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({FilmNotFoundException.class,
            UserNotFoundException.class,
            GenreNotFoundException.class,
            MpaNotFoundException.class,
            DirectorNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(final RuntimeException e) {
        return Map.of(
                "error", e.getMessage()
        );
    }

    @ExceptionHandler({FilmAlreadyExistsException.class,
            UserAlreadyExistsException.class,
            DirectorAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleAlreadyExists(final RuntimeException e) {
        return Map.of(
                "error", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(final ValidationException e) {
        return Map.of(
                "error", e.getMessage()
        );
    }
}
