package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    @DisplayName("Создание пользователя с корректными данными")
    void createUserWithValidData() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userController.create(user);

        assertTrue(created.getId() > 0);
        assertEquals("user@example.com", created.getEmail());
        assertEquals("Имя", created.getName());
    }

    @Test
    @DisplayName("Подстановка login в name, если name пустое")
    void shouldUseLoginIfNameEmpty() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setName("");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userController.create(user);

        assertEquals("login", created.getName());
    }

    @Test
    @DisplayName("Ошибка: пустой email")
    void shouldThrowWhenEmailEmpty() {
        User user = new User();
        user.setEmail("");
        user.setLogin("login");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.create(user));
        assertEquals("Имейл должен быть указан", exception.getMessage());
    }

    @Test
    @DisplayName("Ошибка: email без @")
    void shouldThrowWhenEmailInvalid() {
        User user = new User();
        user.setEmail("userexample.com");
        user.setLogin("login");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.create(user));
        assertEquals("Имейл должен содержать '@'", exception.getMessage());
    }

    @Test
    @DisplayName("Ошибка: пустой или с пробелами login")
    void shouldThrowWhenLoginInvalid() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("bad login");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.create(user));
        assertEquals("Логин не может быть пустым или содержать пробелы", exception.getMessage());
    }

    @Test
    @DisplayName("Ошибка: birthday в будущем")
    void shouldThrowWhenBirthdayInFuture() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setName("Имя");
        user.setBirthday(LocalDate.now().plusDays(1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.create(user));
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }
}
