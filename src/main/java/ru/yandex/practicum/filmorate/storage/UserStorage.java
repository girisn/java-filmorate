package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User add(User user) throws ValidationException;

    User update(User user) throws ValidationException;

    List<User> list();

    boolean isExist(Integer id);
    boolean contains(User user);

    User get(Integer id);

    boolean userHasFriend(Integer userId, Integer friendId);

    void addFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer userId, Integer friendId);

    List<User> friendsList(Integer userId);

    List<User> commonFriendsList(Integer userId, Integer otherUserId);
}
