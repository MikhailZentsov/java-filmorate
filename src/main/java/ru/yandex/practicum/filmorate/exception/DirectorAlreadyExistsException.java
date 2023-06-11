package ru.yandex.practicum.filmorate.exception;

public class DirectorAlreadyExistsException extends RuntimeException {
    public DirectorAlreadyExistsException(String message) {
        super(message);
    }
}
