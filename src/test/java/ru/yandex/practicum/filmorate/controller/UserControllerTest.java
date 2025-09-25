package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.model.User;

import jakarta.validation.Validator;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private static Validator validator;
    private User user;
    private UserController userController;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @BeforeEach
    void setUpController() {
        userController = new UserController();
        user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    @DisplayName("Создание пользователя с корректными данными")
    void createUserWithValidData() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Не должно быть ошибок валидации для корректных данных");
    }

    @Test
    @DisplayName("Подстановка login в name, если name пустое")
    void shouldUseLoginIfNameEmpty() {
        user.setName("");
        userController.setNameIfBlank(user);
        assertEquals(user.getLogin(), user.getName(), "Если имя пустое, должно использоваться login");
    }

    @Test
    @DisplayName("Ошибка: пустой email")
    void shouldThrowWhenEmailEmpty() {
        user.setEmail("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Должна быть ошибка валидации для пустого email");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Имейл должен быть указан")));
    }

    @Test
    @DisplayName("Ошибка: email без @")
    void shouldThrowWhenEmailInvalid() {
        user.setEmail("userexample.com");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("должен содержать '@'")));
    }

    @Test
    @DisplayName("Ошибка: пустой или с пробелами login")
    void shouldThrowWhenLoginInvalid() {
        user.setLogin("bad login");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Должна быть ошибка валидации для login с пробелами");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("не может быть пустым или содержать пробелы")));
    }

    @Test
    @DisplayName("Ошибка: birthday в будущем")
    void shouldThrowWhenBirthdayInFuture() {
        user.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("не может быть в будущем")));
    }
}
