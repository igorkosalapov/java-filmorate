package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Film film;

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
    }

    @Test
    @DisplayName("Создание фильма с корректными данными")
    void createFilmWithValidData() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Фильм"));
    }

    @Test
    @DisplayName("Ошибка: пустое название фильма")
    void shouldThrowWhenFilmNameIsEmpty() throws Exception {
        film.setName("");
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        result.getResolvedException().getMessage()
                                .contains("Название не может быть пустым"));
    }

    @Test
    @DisplayName("Ошибка: описание больше 200 символов")
    void shouldThrowWhenDescriptionTooLong() throws Exception {
        film.setDescription("x".repeat(201));
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        result.getResolvedException().getMessage()
                                .contains("Описание не может превышать 200 символов"));
    }

    @Test
    @DisplayName("Ошибка: дата релиза раньше 28.12.1895")
    void shouldThrowWhenReleaseDateTooEarly() throws Exception {
        Film film = new Film();
        filmController = new FilmController();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(100);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );
        assertEquals("Дата релиза — не раньше 1895-12-28", exception.getMessage());
    }

    @Test
    @DisplayName("Ошибка: продолжительность <= 0")
    void shouldThrowWhenDurationInvalid() throws Exception {
        film.setDuration(0);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        result.getResolvedException().getMessage()
                                .contains("Продолжительность фильма должна быть положительным числом"));
    }
}
