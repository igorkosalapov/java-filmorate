package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    @Qualifier("genreDbStorage")
    private final GenreStorage genreStorage;

    @Qualifier("mpaDbStorage")
    private final MpaStorage mpaStorage;

    public Collection<Film> findAll() {
        Collection<Film> films = filmStorage.findAll();

        Set<Long> ids = films.stream()
                .map(Film::getId)
                .collect(Collectors.toSet());

        Map<Long, List<Genre>> genresByFilm = genreStorage.findGenresForFilms(ids);

        films.forEach(film -> {
            TreeSet<Genre> sorted = new TreeSet<>(Comparator.comparingLong(Genre::getId));
            sorted.addAll(genresByFilm.getOrDefault(film.getId(), List.of()));
            film.setGenres(sorted);
        });

        return films;
    }

    public Film create(Film film) {
        validate(film);

        mpaStorage.findById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("MPA не найден"));

        film.setGenres(validateGenres(film.getGenres()));

        filmStorage.create(film);
        return findById(film.getId());
    }

    public Film update(Film film) {
        validate(film);

        filmStorage.findById(film.getId())
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        mpaStorage.findById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("MPA не найден"));

        film.setGenres(validateGenres(film.getGenres()));

        filmStorage.update(film);
        return findById(film.getId());
    }

    public Film findById(long id) {
        Film film = filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));

        List<Genre> genres = genreStorage.findGenresByFilmId(id);

        TreeSet<Genre> sorted = new TreeSet<>(Comparator.comparingLong(Genre::getId));
        sorted.addAll(genres);

        film.setGenres(sorted);

        return film;
    }

    public void addLike(long filmId, long userId) {
        findById(filmId);
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        findById(filmId);
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopular(int count) {
        List<Film> films = filmStorage.getPopular(count);

        Set<Long> ids = films.stream()
                .map(Film::getId)
                .collect(Collectors.toSet());

        Map<Long, List<Genre>> genresByFilm = genreStorage.findGenresForFilms(ids);

        films.forEach(film -> {
            TreeSet<Genre> sorted = new TreeSet<>(Comparator.comparingLong(Genre::getId));
            sorted.addAll(genresByFilm.getOrDefault(film.getId(), List.of()));
            film.setGenres(sorted);
        });

        return films;
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание слишком длинное");
        }
        if (film.getReleaseDate() == null ||
                film.getReleaseDate().isBefore(java.time.LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Слишком ранняя дата релиза");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительной");
        }
    }

    private Set<Genre> validateGenres(Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return Set.of();
        }

        Set<Long> ids = genres.stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        List<Genre> existing = genreStorage.findByIds(ids);

        if (existing.size() != ids.size()) {
            throw new NotFoundException("Некоторые жанры не существуют");
        }

        return existing.stream()
                .collect(Collectors.toCollection(
                        () -> new TreeSet<>(Comparator.comparingLong(Genre::getId))
                ));
    }
}
