package ru.yandex.practicum.filmorate.exception;

public class ReviewAlreadyExistsException extends RuntimeException {

    public ReviewAlreadyExistsException(String message) {
        super(message);
    }
}
