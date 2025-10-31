package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {

    @Autowired
    private FilmController filmController;

    @Autowired
    private UserStorage userStorage;

    private Film film;

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setName("Test film");
        film.setDescription("Test description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
    }

    @Test
    void createAndFindAll_shouldWork() {
        Film created = filmController.create(film);

        assertNotNull(created.getId());
        assertEquals("Test film", created.getName());

        List<Film> all = new ArrayList<>(filmController.findAll());
        assertEquals(1, all.size());
        assertEquals(created.getId(), all.getFirst().getId());
    }

    @Test
    void createAndUpdate_shouldWork() {
        Film created = filmController.create(film);
        created.setName("Updated film");
        Film updated = filmController.update(created);

        assertEquals(created.getId(), updated.getId());
        assertEquals("Updated film", updated.getName());
    }

    @Test
    void addAndRemoveLike_shouldWork() {
        Film created = filmController.create(film);

        User user = new User();
        user.setLogin("user1");
        user.setEmail("user1@mail.com");
        user.setName("User One");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User createdUser = userStorage.create(user);

        filmController.addLike(created.getId(), createdUser.getId());
        assertTrue(filmController.findById(created.getId()).getLikes().contains(createdUser.getId()));

        filmController.removeLike(created.getId(), createdUser.getId());
        assertFalse(filmController.findById(created.getId()).getLikes().contains(createdUser.getId()));
    }

    @Test
    void getPopular_shouldReturnSorted() {
        Film f1 = filmController.create(film);

        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Desc 2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(110);
        Film f2 = filmController.create(film2);


        User u1 = new User();
        u1.setLogin("u1");
        u1.setEmail("u1@mail.com");
        u1.setName("User One");
        u1.setBirthday(LocalDate.of(1990, 1, 1));
        User createdUser1 = userStorage.create(u1);

        User u2 = new User();
        u2.setLogin("u2");
        u2.setEmail("u2@mail.com");
        u2.setName("User Two");
        u2.setBirthday(LocalDate.of(1995, 5, 5));
        User createdUser2 = userStorage.create(u2);

        filmController.addLike(f1.getId(), createdUser1.getId());
        filmController.addLike(f2.getId(), createdUser1.getId());
        filmController.addLike(f2.getId(), createdUser2.getId());

        List<Film> popular = filmController.getPopular(2);
        assertEquals(2, popular.size());
        assertEquals(f2.getId(), popular.get(0).getId());
        assertEquals(f1.getId(), popular.get(1).getId());
    }
}