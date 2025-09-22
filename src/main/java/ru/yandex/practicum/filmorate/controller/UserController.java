package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

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
    public User create(@Valid @RequestBody User user) {
        user.setId(getNextId());
        setNameIfBlank(user);
        users.put(user.getId(), user);

        log.info("Пользователь добавлен: {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User updatedUser) {
        if (updatedUser.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        User existing = users.get(updatedUser.getId());
        if (existing == null) {
            throw new ValidationException("Пользователь с id = " + updatedUser.getId() + " не найден.");
        }

        existing.setEmail(updatedUser.getEmail());
        existing.setLogin(updatedUser.getLogin());
        existing.setBirthday(updatedUser.getBirthday());
        existing.setName(updatedUser.getName());
        setNameIfBlank(existing);

        log.info("Пользователь обновлён: {}", existing);
        return existing;
    }

    private void setNameIfBlank(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
