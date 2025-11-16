package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friendship {
    private Long userId;        // инициатор
    private Long friendId;      // получатель
    private FriendshipStatus status;
    private Instant requestedAt;
}
