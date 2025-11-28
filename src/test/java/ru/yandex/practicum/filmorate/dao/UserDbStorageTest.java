package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class})
class UserDbStorageTest {

    @Autowired
    private UserDbStorage storage;

    private User makeUser() {
        return new User(
                null,
                "mail@mail.com",
                "login",
                "User",
                LocalDate.of(1990, 1, 1),
                new java.util.HashSet<>()
        );
    }

    @Test
    void createAndFindById_shouldWork() {
        User created = storage.create(makeUser());
        Optional<User> fromDb = storage.findById(created.getId());

        assertThat(fromDb)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("email", "mail@mail.com")
                );
    }

    @Test
    void findAll_shouldReturnList() {
        storage.create(makeUser());
        storage.create(new User(
                null, "a@a", "A", "User2",
                LocalDate.of(1991,1,1),
                new java.util.HashSet<>()
        ));

        assertThat(storage.findAll()).hasSize(2);
    }

    @Test
    void update_shouldModifyUser() {
        User u = storage.create(makeUser());
        u.setName("Updated");

        storage.update(u);

        Optional<User> updated = storage.findById(u.getId());
        assertThat(updated)
                .isPresent()
                .hasValueSatisfying(x ->
                        assertThat(x).hasFieldOrPropertyWithValue("name", "Updated")
                );
    }
}
