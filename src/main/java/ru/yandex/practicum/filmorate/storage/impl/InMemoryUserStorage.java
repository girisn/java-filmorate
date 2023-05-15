package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 1;

    @Override
    public User add(User user) throws ValidationException {
        user.setId(id);
        users.put(id, user);
        id++;

        return user;
    }

    @Override
    public User update(User user) throws ValidationException {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> list() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean isExist(Integer id) {
        return users.containsKey(id);
    }

    @Override
    public User get(Integer id) {
        return users.get(id);
    }

    @Override
    public boolean userHasFriend(Integer userId, Integer friendId) {
        return users.get(userId).getFriends().contains(friendId);
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        User user = users.get(userId);
        user.getFriends().add(friendId);
        User friend = users.get(friendId);
        friend.getFriends().add(userId);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        User user = users.get(userId);
        user.getFriends().remove(friendId);
        User friend = users.get(friendId);
        friend.getFriends().remove(userId);
    }

    @Override
    public List<User> friendsList(Integer userId) {
        Set<Integer> friendIds = users.get(userId).getFriends();
        return friendIds.stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> commonFriendsList(Integer userId, Integer otherUserId) {
        Set<Integer> userFriendsList = users.get(userId).getFriends();
        Set<Integer> otherUserFriendsList = users.get(otherUserId).getFriends();

        return userFriendsList.stream()
                .filter(otherUserFriendsList::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public boolean contains(User user) {
        return users.containsKey(user.getId());
    }
}
