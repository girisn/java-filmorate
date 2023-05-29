package ru.yandex.practicum.filmorate.storage.impl.inmemory;

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
    public User add(User user) {
        user.setId(id);
        users.put(id, user);
        id++;

        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> list() {
        return new ArrayList<>(users.values());
    }


    public boolean isExist(Integer id) {
        return users.containsKey(id);
    }

    @Override
    public Optional<User> getById(Integer id) {
        return Optional.of(users.get(id));
    }

    @Override
    public Optional<User> getFriendById(Integer userId, Integer friendId) {
        return null;
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        user.addFriend(friend);
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
        Set<Integer> friendIds = users.get(userId).getFriends().keySet();
        return friendIds.stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> commonFriendsList(Integer userId, Integer otherUserId) {
        Set<Integer> userFriendsList = users.get(userId).getFriends().keySet();
        Set<Integer> otherUserFriendsList = users.get(otherUserId).getFriends().keySet();

        return userFriendsList.stream()
                .filter(otherUserFriendsList::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }


    public boolean contains(User user) {
        return users.containsKey(user.getId());
    }
}
