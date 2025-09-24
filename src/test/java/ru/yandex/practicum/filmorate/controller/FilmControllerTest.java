package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private static Validator validator;
    private FilmController filmController;
    private Film film;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @BeforeEach
    void setUpController() {
        filmController = new FilmController();
        film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
    }

    @Test
    @DisplayName("Тест с правильными данными")
    void createFilmWithValidData() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Не должно быть ошибок валидации для корректных данных");
    }

    @Test
    @DisplayName("Ошибка: пустое название фильма")
    void shouldThrowWhenFilmNameIsEmpty() {
        film.setName("");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("не может быть пустым")));
    }

    @Test
    @DisplayName("Ошибка: описание больше 200 символов")
    void shouldThrowWhenDescriptionTooLong() {
        film.setDescription("x".repeat(201));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage()
                .contains("не может превышать 200 символов")));
    }

    @Test
    @DisplayName("Ошибка: дата релиза раньше 28.12.1895")
    void shouldThrowWhenReleaseDateTooEarly() {
        film.setReleaseDate(LocalDate.of(1800, 1, 1));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );
        assertTrue(exception.getMessage().contains("Дата релиза"));
    }

    @Test
    @DisplayName("Ошибка: продолжительность меньше или равна нулю")
    void shouldThrowWhenDurationInvalid() {
        film.setDuration(0);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("положительной")));
    }
}
