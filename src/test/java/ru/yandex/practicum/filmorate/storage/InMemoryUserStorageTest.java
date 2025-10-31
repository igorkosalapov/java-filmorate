package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageTest {

    private InMemoryUserStorage storage;

    @BeforeEach
    void setUp() {
        storage = new InMemoryUserStorage();
    }

    // Вспомогательный метод для создания тестового пользователя
    private User createTestUser() {
        return new User(
                null,
                "mail@mail.ru",
                "login",
                "User1",
                LocalDate.of(1990, 1, 1),
                new HashSet<>()
        );
    }

    @Test
    void create_shouldAddUserAndGenerateId() {
        User createdUser = storage.create(createTestUser());

        assertNotNull(createdUser.getId(), "ID должен быть присвоен автоматически");
        assertEquals("User1", createdUser.getName(), "Имя пользователя должно совпадать");
        assertEquals("mail@mail.ru", createdUser.getEmail(), "Email должен совпадать");
        assertEquals("login", createdUser.getLogin(), "Login должен совпадать");
    }

    @Test
    void update_shouldReplaceExistingUser() {
        User user = storage.create(createTestUser());
        user.setName("Updated");
        storage.update(user);

        Optional<User> updated = storage.findById(user.getId());
        assertTrue(updated.isPresent(), "Пользователь должен существовать после обновления");
        assertEquals("Updated", updated.get().getName(), "Имя должно быть обновлено");
    }

    @Test
    void findById_shouldReturnExistingUser() {
        User user = storage.create(createTestUser());
        Optional<User> found = storage.findById(user.getId());

        assertTrue(found.isPresent(), "Пользователь должен быть найден по ID");
        assertEquals(user.getId(), found.get().getId(), "ID найденного пользователя должен совпадать");
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        User user1 = storage.create(createTestUser());
        User user2 = storage.create(new User(
                null,
                "mail2@mail.ru",
                "login2",
                "User2",
                LocalDate.of(1995, 5, 5),
                new HashSet<>()
        ));

        assertEquals(2, storage.findAll().size(), "Должны быть возвращены все пользователи");
    }
}
