package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    User create(User user);

    User update(User user);

    List<User> findAll();

    Optional<User> findById(Long id);

    List<User> findByIds(Set<Long> ids);
}
