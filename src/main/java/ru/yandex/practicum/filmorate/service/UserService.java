package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    @Qualifier("friendshipDbStorage")
    private final FriendshipStorage friendshipStorage;

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        getById(user.getId());
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
        getById(id);
        getById(friendId);
        friendshipStorage.addFriend(id, friendId);
    }

    public void removeFriend(Long id, Long friendId) {
        getById(id);
        getById(friendId);
        friendshipStorage.removeFriend(id, friendId);
    }

    public List<User> getFriends(Long id) {
        getById(id);
        return friendshipStorage.getFriends(id)
                .stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        getById(id);
        getById(otherId);
        return friendshipStorage.getCommonFriends(id, otherId)
                .stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }
}
