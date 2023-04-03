package ru.yandex.practicum.filmorate.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ValidationException extends ResponseStatusException {

    public ValidationException(HttpStatus status,String message) {
        super(status, message);
    }
}
