package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private InMemoryUserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
    }

    // Вспомогательный метод для создания пользователя
    private User createTestUser(String email, String login, String name, LocalDate birthday) {
        return new User(null, email, login, name, birthday, new HashSet<>());
    }

    @Test
    void addFriend_shouldMakeFriendshipMutual() {
        User user1 = userStorage.create(createTestUser("mail1@mail.ru", "login1", "User1", LocalDate.of(1990, 1, 1)));
        User user2 = userStorage.create(createTestUser("mail2@mail.ru", "login2", "User2", LocalDate.of(1991, 2, 2)));

        userService.addFriend(user1.getId(), user2.getId());

        assertTrue(userStorage.findById(user1.getId()).orElseThrow().getFriends().contains(user2.getId()));
        assertTrue(userStorage.findById(user2.getId()).orElseThrow().getFriends().contains(user1.getId()));
    }

    @Test
    void removeFriend_shouldRemoveFromBothSides() {
        User user1 = userStorage.create(createTestUser("mail1@mail.ru", "login1", "User1", LocalDate.of(1990, 1, 1)));
        User user2 = userStorage.create(createTestUser("mail2@mail.ru", "login2", "User2", LocalDate.of(1991, 2, 2)));

        userService.addFriend(user1.getId(), user2.getId());
        userService.removeFriend(user1.getId(), user2.getId());

        assertFalse(userService.getById(user1.getId()).getFriends().contains(user2.getId()));
        assertFalse(userService.getById(user2.getId()).getFriends().contains(user1.getId()));
    }

    @Test
    void getFriends_shouldReturnAllFriends() {
        User user1 = userStorage.create(createTestUser("mail1@mail.ru", "login1", "User1", LocalDate.of(1990, 1, 1)));
        User user2 = userStorage.create(createTestUser("mail2@mail.ru", "login2", "User2", LocalDate.of(1991, 2, 2)));
        User user3 = userStorage.create(createTestUser("mail3@mail.ru", "login3", "User3", LocalDate.of(1992, 3, 3)));

        userService.addFriend(user1.getId(), user2.getId());
        userService.addFriend(user1.getId(), user3.getId());

        List<User> friends = userService.getFriends(user1.getId());
        assertEquals(2, friends.size());
        assertTrue(friends.contains(user2));
        assertTrue(friends.contains(user3));
    }

    @Test
    void getCommonFriends_shouldReturnIntersection() {
        User user1 = userStorage.create(createTestUser("mail1@mail.ru", "login1", "User1", LocalDate.of(1990, 1, 1)));
        User user2 = userStorage.create(createTestUser("mail2@mail.ru", "login2", "User2", LocalDate.of(1991, 2, 2)));
        User user3 = userStorage.create(createTestUser("mail3@mail.ru", "login3", "User3", LocalDate.of(1992, 3, 3)));

        userService.addFriend(user1.getId(), user3.getId());
        userService.addFriend(user2.getId(), user3.getId());

        List<User> common = userService.getCommonFriends(user1.getId(), user2.getId());
        assertEquals(1, common.size());
        assertTrue(common.contains(user3));
    }
}
