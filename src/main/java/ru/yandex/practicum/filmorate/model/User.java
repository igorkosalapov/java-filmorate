package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private Integer id;

    @Email(message = "Имейл должен содержать '@'")
    @NotBlank(message = "Имейл должен быть указан")
    private String email;

    @Pattern(regexp = "\\S+", message = "Логин не может быть пустым или содержать пробелы")
    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
