package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("friendshipDbStorage")
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbc;

    @Override
    public void addFriend(Long userId, Long friendId) {
        jdbc.update(
                "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)",
                userId, friendId
        );
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        jdbc.update(
                "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?",
                userId, friendId
        );
    }

    @Override
    public List<Long> getFriends(Long userId) {
        return jdbc.query(
                "SELECT friend_id FROM friendships WHERE user_id = ?",
                (rs, rowNum) -> rs.getLong("friend_id"),
                userId
        );
    }

    @Override
    public List<Long> getCommonFriends(Long userId, Long otherId) {
        String sql = """
            SELECT f1.friend_id
            FROM friendships f1
            JOIN friendships f2 ON f1.friend_id = f2.friend_id
            WHERE f1.user_id = ? AND f2.user_id = ?
        """;
        return jdbc.query(sql,
                (rs, rowNum) -> rs.getLong("friend_id"),
                userId, otherId
        );
    }
}
