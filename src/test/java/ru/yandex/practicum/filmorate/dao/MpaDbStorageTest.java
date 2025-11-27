package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({
        MpaDbStorage.class,
        MpaRowMapper.class
})
class MpaDbStorageTest {

    @Autowired
    private MpaDbStorage storage;

    @Test
    void findAll_shouldReturnFive() {
        assertThat(storage.findAll()).hasSize(5);
    }

    @Test
    void findById_shouldReturnCorrectRating() {
        assertThat(storage.findById(1L))
                .isPresent()
                .hasValueSatisfying(r ->
                        assertThat(r).hasFieldOrPropertyWithValue("id", 1L)
                );
    }
}
