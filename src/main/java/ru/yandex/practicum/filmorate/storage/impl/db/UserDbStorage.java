package ru.yandex.practicum.filmorate.storage.impl.db;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

public class UserDbStorage implements UserStorage {
    @Override
    public User add(User user) throws ValidationException {
        return null;
    }

    @Override
    public User update(User user) throws ValidationException {
        return null;
    }

    @Override
    public List<User> list() {
        return null;
    }

    @Override
    public boolean isExist(Integer id) {
        return false;
    }

    @Override
    public boolean contains(User user) {
        return false;
    }

    @Override
    public User get(Integer id) {
        return null;
    }

    @Override
    public boolean userHasFriend(Integer userId, Integer friendId) {
        return false;
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {

    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {

    }

    @Override
    public List<User> friendsList(Integer userId) {
        return null;
    }

    @Override
    public List<User> commonFriendsList(Integer userId, Integer otherUserId) {
        return null;
    }
}
