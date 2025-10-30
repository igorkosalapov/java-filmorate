package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFilmStorageTest {

    private InMemoryFilmStorage storage;

    @BeforeEach
    void setUp() {
        storage = new InMemoryFilmStorage();
    }

    private Film createTestFilm() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        return film;
    }

    @Test
    void create_shouldAddFilmAndGenerateId() {
        Film film = storage.create(createTestFilm());
        assertNotNull(film.getId(), "ID должен быть присвоен автоматически");
        assertEquals(1, storage.findAll().size(), "Фильм должен добавляться в хранилище");
    }

    @Test
    void update_shouldReplaceExistingFilm() {
        Film film = storage.create(createTestFilm());
        film.setName("Updated");
        storage.update(film);

        Optional<Film> updated = storage.findById(film.getId());
        assertTrue(updated.isPresent(), "Фильм должен быть найден");
        assertEquals("Updated", updated.get().getName(), "Имя должно обновиться");
    }

    @Test
    void findById_shouldReturnExistingFilm() {
        Film film = storage.create(createTestFilm());
        Optional<Film> found = storage.findById(film.getId());
        assertTrue(found.isPresent(), "Фильм должен быть найден по ID");
    }
}
