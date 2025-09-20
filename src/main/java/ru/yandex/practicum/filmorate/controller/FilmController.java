package ru.yandex.practicum.filmorate.controller;

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
    public Film create(@RequestBody Film film) {
        try {
            validateFilm(film);
            film.setId(getNextId());
            films.put(film.getId(), film);
            log.info("Фильм добавлен: {}", film);
            return film;
        } catch (ValidationException e) {
            log.error("Ошибка добавления фильма: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping
    public Film update(@RequestBody Film updatedFilm) {
        try {
            if (updatedFilm.getId() == 0) {
                throw new ValidationException("Id должен быть указан.");
            }

            Film existing = films.get(updatedFilm.getId());

            if (existing == null) {
                throw new ValidationException("Фильм с id = " + updatedFilm.getId() + " не найден.");
            }
            validateFilm(updatedFilm);

            existing.setName(updatedFilm.getName());
            existing.setDescription(updatedFilm.getDescription());
            existing.setReleaseDate(updatedFilm.getReleaseDate());
            existing.setDuration(updatedFilm.getDuration());
            log.info("Фильм обновлён: {}", existing);
            return existing;
        } catch (ValidationException e) {
            log.error("Ошибка обновления фильма: {}", e.getMessage());
            throw e;
        }
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым.");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Длина описания не должна быть больше 200 символов.");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Дата релиза — не раньше " + CINEMA_BIRTHDAY);
        }
        if (film.getDuration() <= 0 ) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }
    }
}
