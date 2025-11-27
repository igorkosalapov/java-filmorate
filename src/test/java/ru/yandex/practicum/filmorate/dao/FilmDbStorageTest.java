package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class,
        FilmRowMapper.class,

        GenreDbStorage.class,
        GenreRowMapper.class,

        MpaDbStorage.class,
        MpaRowMapper.class})
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage storage;

    private Film makeFilm() {
        return new Film(
                null,
                "Film",
                "desc",
                LocalDate.of(2000, 1, 1),
                120,
                new MpaRating(1L, "G"),
                new HashSet<>(),
                new HashSet<>()
        );
    }

    @Test
    void createAndFindById_shouldWork() {
        Film created = storage.create(makeFilm());
        Optional<Film> fromDb = storage.findById(created.getId());

        assertThat(fromDb)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("name", "Film")
                );
    }

    @Test
    void findAll_shouldReturnList() {
        storage.create(makeFilm());
        storage.create(new Film(null, "F2", "d",
                LocalDate.of(2001, 1, 1), 100,
                new MpaRating(1L, "G"),
                new HashSet<>(),
                new HashSet<>()
        ));

        assertThat(storage.findAll()).hasSize(2);
    }

    @Test
    void update_shouldModifyFilm() {
        Film f = storage.create(makeFilm());
        f.setName("Updated");

        storage.update(f);

        Optional<Film> updated = storage.findById(f.getId());
        assertThat(updated)
                .isPresent()
                .hasValueSatisfying(x ->
                        assertThat(x).hasFieldOrPropertyWithValue("name", "Updated")
                );
    }
}
