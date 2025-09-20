package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

    private int getNextId() {
        return idCounter++;
    }

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        try {
            validateUser(user);
            user.setId(getNextId());
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("Пользователь добавлен: {}", user);
            return user;
        } catch (ValidationException e) {
            log.error("Ошибка добавления пользователя: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping
    public User update(@RequestBody User updatedUser) {
        try {
            if (updatedUser.getId() == 0) {
                throw new ValidationException("Id должен быть указан");
            }

            validateUser(updatedUser);

            User existing = users.get(updatedUser.getId());
            if (existing == null) {
                throw new ValidationException("Пользователь с id = " + updatedUser.getId() + " не найден.");
            }

            existing.setEmail(updatedUser.getEmail());
            existing.setName(updatedUser.getName() == null || updatedUser.getName().isBlank() ?
                    updatedUser.getLogin() : updatedUser.getName()
            );
            existing.setLogin(updatedUser.getLogin());
            existing.setBirthday(updatedUser.getBirthday());
            log.info("Пользователь обновлён: {}", existing);
            return existing;
        } catch (ValidationException e) {
            log.error("Ошибка обновления пользователя: {}", e.getMessage());
            throw e;
        }
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Имейл должен быть указан");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Имейл должен содержать '@'");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
