package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({
        GenreDbStorage.class,
        GenreRowMapper.class
})
class GenreDbStorageTest {

    @Autowired
    private GenreDbStorage storage;

    @Test
    void findAll_shouldReturnSix() {
        assertThat(storage.findAll()).hasSize(6);
    }

    @Test
    void findById_shouldReturnCorrectGenre() {
        assertThat(storage.findById(1L))
                .isPresent()
                .hasValueSatisfying(g ->
                        assertThat(g).hasFieldOrPropertyWithValue("id", 1L)
                );
    }
}
