package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        userStorage.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + user.getId() + " не найден"));
        return userStorage.update(user);
    }

    public List<User> getAll() {
        return userStorage.findAll();
    }

    public User getById(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }

    public void addFriend(Long id, Long friendId) {
        User user = getById(id);
        User friend = getById(friendId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        if (friend == null) {
            throw new NotFoundException("Пользователь с id=" + friendId + " не найден");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    public void removeFriend(Long id, Long friendId) {
        User user = getById(id);
        User friend = getById(friendId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        if (friend == null) {
            throw new NotFoundException("Пользователь с id=" + friendId + " не найден");
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    public List<User> getFriends(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        return user.getFriends().stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        Set<Long> friends1 = new HashSet<>(getById(id).getFriends());
        Set<Long> friends2 = new HashSet<>(getById(otherId).getFriends());
        friends1.retainAll(friends2);
        return friends1.stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }
}
