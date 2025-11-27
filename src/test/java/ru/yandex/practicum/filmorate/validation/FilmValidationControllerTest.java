package ru.yandex.practicum.filmorate.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
class FilmValidationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private FilmService filmService;

    private Film baseFilm() {
        return new Film(
                null,
                "Normal Film",
                "Description",
                LocalDate.of(2000, 1, 1),
                100,
                new MpaRating(1L, "G"),
                new HashSet<>(),
                new HashSet<>()
        );
    }

    @Test
    void shouldFailWhenNameBlank() throws Exception {
        Film f = baseFilm();
        f.setName("");

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(f)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailWhenDurationNegative() throws Exception {
        Film f = baseFilm();
        f.setDuration(-5);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(f)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailWhenReleaseBefore1895() throws Exception {
        Film f = baseFilm();
        f.setReleaseDate(LocalDate.of(1700, 1, 1));

        Mockito.doThrow(new ValidationException("Слишком ранняя дата релиза"))
                .when(filmService).create(Mockito.any(Film.class));

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(f)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validFilmShouldPass() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(baseFilm())))
                .andExpect(status().isOk());
    }
}
