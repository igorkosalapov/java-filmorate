package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    @DisplayName("Создание пользователя с корректными данными")
    void createUserWithValidData() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    @DisplayName("Подстановка login в name, если name пустое")
    void shouldUseLoginIfNameEmpty() throws Exception {
        user.setName("");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("login"));
    }

    @Test
    @DisplayName("Ошибка: пустой email")
    void shouldThrowWhenEmailEmpty() throws Exception {
        user.setEmail("");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        result.getResolvedException().getMessage()
                                .contains("Имейл должен быть указан"));
    }

    @Test
    @DisplayName("Ошибка: email без @")
    void shouldThrowWhenEmailInvalid() throws Exception {
        user.setEmail("userexample.com");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        result.getResolvedException().getMessage()
                                .contains("Имейл должен содержать '@'"));
    }

    @Test
    @DisplayName("Ошибка: login с пробелами")
    void shouldThrowWhenLoginInvalid() throws Exception {
        user.setLogin("bad login");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        result.getResolvedException().getMessage()
                                .contains("Логин не может быть пустым или содержать пробелы"));
    }

    @Test
    @DisplayName("Ошибка: birthday в будущем")
    void shouldThrowWhenBirthdayInFuture() throws Exception {
        user.setBirthday(LocalDate.now().plusDays(1));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        result.getResolvedException().getMessage()
                                .contains("Дата рождения не может быть в будущем"));
    }
}
