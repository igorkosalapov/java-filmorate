package ru.yandex.practicum.filmorate.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserValidationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    private User baseUser() {
        return new User(
                null,
                "mail@mail.com",
                "login",
                "User",
                LocalDate.of(1990, 1, 1),
                new HashSet<>()
        );
    }

    @Test
    void shouldFailWhenEmailInvalid() throws Exception {
        User u = baseUser();
        u.setEmail("invalid");

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(u)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailWhenLoginIsBlank() throws Exception {
        User u = baseUser();
        u.setLogin(" ");

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(u)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailWhenBirthdayInFuture() throws Exception {
        User u = baseUser();
        u.setBirthday(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(u)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validUserShouldPass() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(baseUser())))
                .andExpect(status().isOk());
    }
}
