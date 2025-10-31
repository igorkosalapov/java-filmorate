package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;
    InMemoryUserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        userController = new UserController(userService);
    }

    private User makeUser(String email, String login) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(login + "_name");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }

    @Test
    void createAndGetAll_shouldWork() {
        User created = userController.create(makeUser("test@mail.ru", "login1"));
        assertNotNull(created.getId());
        assertEquals("login1", created.getLogin());

        List<User> all = userController.getAll();
        assertEquals(1, all.size());
    }

    @Test
    void update_shouldWork() {
        User created = userController.create(makeUser("t@mail.ru", "login1"));
        created.setName("Updated name");

        User updated = userController.update(created);
        assertEquals("Updated name", updated.getName());
    }

    @Test
    void getById_shouldReturnCorrectUser() {
        User u1 = userController.create(makeUser("a@a", "A"));
        User u2 = userController.create(makeUser("b@b", "B"));

        User found = userController.getById(u1.getId());
        assertEquals(u1.getLogin(), found.getLogin());
    }

    @Test
    void addAndRemoveFriends_shouldWork() {
        User u1 = userController.create(makeUser("1@1", "one"));
        User u2 = userController.create(makeUser("2@2", "two"));

        userController.addFriend(u1.getId(), u2.getId());
        assertTrue(userController.getFriends(u1.getId()).contains(u2));

        userController.removeFriend(u1.getId(), u2.getId());
        assertTrue(userController.getFriends(u1.getId()).isEmpty());
    }

    @Test
    void getCommonFriends_shouldReturnMutuals() {
        User u1 = userController.create(makeUser("1@1", "one"));
        User u2 = userController.create(makeUser("2@2", "two"));
        User u3 = userController.create(makeUser("3@3", "three"));

        userController.addFriend(u1.getId(), u3.getId());
        userController.addFriend(u2.getId(), u3.getId());

        List<User> common = userController.getCommonFriends(u1.getId(), u2.getId());
        assertEquals(1, common.size());
        assertEquals(u3.getId(), common.getFirst().getId());
    }
}
