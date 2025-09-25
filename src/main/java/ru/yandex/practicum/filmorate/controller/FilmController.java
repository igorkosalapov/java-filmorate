package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;
    public static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    private int getNextId() {
        return id++;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validateReleaseDate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Фильм добавлен: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film updatedFilm) {
        if (updatedFilm.getId() == null) {
            throw new ValidationException("Id должен быть указан.");
        }

        Film existing = films.get(updatedFilm.getId());
        if (existing == null) {
            throw new ValidationException("Фильм с id = " + updatedFilm.getId() + " не найден.");
        }

        validateReleaseDate(updatedFilm);

        existing.setName(updatedFilm.getName());
        existing.setDescription(updatedFilm.getDescription());
        existing.setReleaseDate(updatedFilm.getReleaseDate());
        existing.setDuration(updatedFilm.getDuration());

        log.info("Фильм обновлён: {}", existing);
        return existing;
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Дата релиза — не раньше " + CINEMA_BIRTHDAY);
        }
    }
}
