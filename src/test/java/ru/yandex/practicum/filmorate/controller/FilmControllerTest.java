package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {
    private static FilmController filmController;
    private static Film film;
    private Validator validator;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldAddCorrectFilm() {
        filmController.addFilm(film);
        List<Film> listFilms = filmController.getFilms();

        assertEquals(1, listFilms.size(),
                "Количество фильмов не совпадает");

        Film filmFromMap = listFilms.get(0);

        assertEquals(film, filmFromMap,
                "Фильм добавлен с ошибками");
    }

    @Test
    void shouldNotAddWithEmptyNameFilm() {
        film.setName(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(2, violations.size(),
                "Количество сообщений об ошибке отличается от корректного с названием null");
        assertEquals(violations.iterator().next().getMessage(), "Название не может быть пустым");

        film.setName("");
        violations = validator.validate(film);
        violations.iterator().next();

        assertEquals(1, violations.size(),
                "Количество сообщений об ошибке отличается от корректного с пустым названием");
        assertEquals(violations.iterator().next().getMessage(), "Название не может быть пустым");

        film.setName("   ");
        violations = validator.validate(film);
        violations.iterator().next();

        assertEquals(1, violations.size(),
                "Количество сообщений об ошибке отличается от корректного с пустым названием");
        assertEquals(violations.iterator().next().getMessage(), "Название не может быть пустым");
    }

    @Test
    void shouldNotAddWithMoreThen200SymbolsDescriptionFilm() {
        film.setDescription(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size(),
                "Возникают ошибки с описанием null");

        film.setDescription(
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789");
        violations = validator.validate(film);
        assertEquals(0, violations.size(),
                "Возникают ошибки с описанием длинной 199");

        film.setDescription(
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
        violations = validator.validate(film);
        assertEquals(0, violations.size(),
                "Возникают ошибки с описанием длинной 200");

        film.setDescription(
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1");
        violations = validator.validate(film);
        assertEquals(1, violations.size(),
                "Не возникают ошибки с описанием длинной 201");
        assertEquals(violations.iterator().next().getMessage(), "Максимальная длина описания 200 символов");
    }

    @Test
    void shouldNotAddTooOldFilm() {
        final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12,28);
        final LocalDate LESS_THEN_MIN_RELEASE_DATE = LocalDate.of(1895, 12,27);
        final LocalDate RIGHT_RELEASE_DATE = LocalDate.of(1895, 12,29);

        film.setReleaseDate(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size(),
                "Возникают ошибки с датой релиза null");

        film.setReleaseDate(RIGHT_RELEASE_DATE);
        violations = validator.validate(film);
        assertEquals(0, violations.size(),
                "Возникают ошибки с корректной датой релиза");

        film.setReleaseDate(MIN_RELEASE_DATE);
        violations = validator.validate(film);
        assertEquals(0, violations.size(),
                "Возникают ошибки с корректной датой релиза");

        film.setReleaseDate(LESS_THEN_MIN_RELEASE_DATE);
        violations = validator.validate(film);
        assertEquals(1, violations.size(),
                "Не возникают ошибки с не корректной датой релиза");
        assertEquals(violations.iterator().next().getMessage(),
                "Дата не может быть раньше создания первого фильма в истории");
    }

    @Test
    void shouldNotAddFilmWithNegativeDuration() {
        film.setDuration(null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size(),
                "Возникают ошибки с длительностью null");

        film.setDuration(1);
        violations = validator.validate(film);
        assertEquals(0, violations.size(),
                "Возникают ошибки с положительной длительностью");

        film.setDuration(0);
        violations = validator.validate(film);
        assertEquals(1, violations.size(),
                "Не возникают ошибки с длительностью ZERO");
        assertEquals(violations.iterator().next().getMessage(),
                "Длительность не может быть отрицательной или нулевой");

        film.setDuration(-1);
        violations = validator.validate(film);
        assertEquals(1, violations.size(),
                "Не возникают ошибки с отрицательной длительностью");
        assertEquals(violations.iterator().next().getMessage(),
                "Длительность не может быть отрицательной или нулевой");
    }
}