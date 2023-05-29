package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User add(User user);

    User update(User user);

    List<User> list();

    Optional<User> getById(Integer id);

    Optional<User> getFriendById(Integer userId, Integer friendId);

    void addFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer userId, Integer friendId);

    List<User> friendsList(Integer userId);

    List<User> commonFriendsList(Integer userId, Integer otherUserId);
}
