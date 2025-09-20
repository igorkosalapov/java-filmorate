package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    @DisplayName("Тест с правильными данными")
    void createFilmWithValidData() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film created = filmController.create(film);
        assertTrue(created.getId() > 0);
        assertEquals("Фильм", created.getName());
    }

    @Test
    @DisplayName("Ошибка: пустое название фильма")
    void shouldThrowWhenFilmNameIsEmpty() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.create(film));
        assertEquals("Название не может быть пустым.", exception.getMessage());
    }

    @Test
    @DisplayName("Ошибка: описание больше 200 символов")
    void shouldThrowWhenDescriptionTooLong() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("x".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.create(film));
        assertEquals("Длина описания не должна быть больше 200 символов.", exception.getMessage());
    }

    @Test
    @DisplayName("Ошибка: дата релиза раньше 28.12.1895")
    void shouldThrowWhenReleaseDateTooEarly() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(100);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.create(film));
        assertTrue(exception.getMessage().contains("Дата релиза"));
    }

    @Test
    @DisplayName("Ошибка: продолжительность меньше или равна нулю")
    void shouldThrowWhenDurationInvalid() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.create(film));
        assertEquals("Продолжительность фильма должна быть положительным числом.", exception.getMessage());
    }
}
