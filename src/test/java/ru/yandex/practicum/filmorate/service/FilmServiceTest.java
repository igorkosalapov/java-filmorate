package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {

    private FilmService filmService;
    private InMemoryFilmStorage filmStorage;
    private InMemoryUserStorage userStorage;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage); // теперь 2 аргумента
    }

    private Film createTestFilm(String name, String desc, LocalDate releaseDate, int duration) {
        Film film = new Film();
        film.setName(name);
        film.setDescription(desc);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        return film;
    }

    private void createTestUser(long id, String email, String login, String name) {
        userStorage.create(new ru.yandex.practicum.filmorate.model.User(id, email, login, name, LocalDate.of(1990, 1, 1), new java.util.HashSet<>()));
    }

    @Test
    void addLike_shouldIncreaseLikeCount() {
        Film film = filmStorage.create(createTestFilm("Film1", "Desc", LocalDate.of(2000, 1, 1), 100));
        createTestUser(1L, "mail1@mail.ru", "login1", "User1");

        filmService.addLike(film.getId(), 1L);

        assertTrue(filmStorage.findById(film.getId()).orElseThrow().getLikes().contains(1L));
    }

    @Test
    void removeLike_shouldDecreaseLikeCount() {
        Film film = filmStorage.create(createTestFilm("Film1", "Desc", LocalDate.of(2000, 1, 1), 100));
        createTestUser(1L, "mail1@mail.ru", "login1", "User1");

        filmService.addLike(film.getId(), 1L);
        filmService.removeLike(film.getId(), 1L);

        assertFalse(filmStorage.findById(film.getId()).orElseThrow().getLikes().contains(1L));
    }

    @Test
    void getMostPopularFilms_shouldReturnSortedList() {
        Film f1 = filmStorage.create(createTestFilm("Film1", "Desc", LocalDate.of(2000, 1, 1), 100));
        Film f2 = filmStorage.create(createTestFilm("Film2", "Desc", LocalDate.of(2001, 1, 1), 100));
        Film f3 = filmStorage.create(createTestFilm("Film3", "Desc", LocalDate.of(2002, 1, 1), 100));

        // создаём пользователей
        createTestUser(1L, "mail1@mail.ru", "login1", "User1");
        createTestUser(2L, "mail2@mail.ru", "login2", "User2");
        createTestUser(3L, "mail3@mail.ru", "login3", "User3");

        // добавляем лайки
        filmService.addLike(f1.getId(), 1L);

        filmService.addLike(f2.getId(), 1L);
        filmService.addLike(f2.getId(), 2L);

        filmService.addLike(f3.getId(), 1L);
        filmService.addLike(f3.getId(), 2L);
        filmService.addLike(f3.getId(), 3L);

        List<Film> popular = filmService.getPopular(2);

        assertEquals(2, popular.size());
        assertEquals(f3.getId(), popular.get(0).getId());
        assertEquals(f2.getId(), popular.get(1).getId());
    }
}
