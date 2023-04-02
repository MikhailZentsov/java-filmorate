package ru.yandex.practicum.filmorate.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ValidationException extends RuntimeException{

    public ValidationException(String message) {
        super(message);
    }
}
