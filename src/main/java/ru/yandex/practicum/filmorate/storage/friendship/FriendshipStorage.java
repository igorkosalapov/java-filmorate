package ru.yandex.practicum.filmorate.storage.friendship;

import java.util.List;

public interface FriendshipStorage {

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<Long> getFriends(Long userId);

    List<Long> getCommonFriends(Long userId, Long otherUserId);
}
