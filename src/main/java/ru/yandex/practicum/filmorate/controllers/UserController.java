package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController

public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public User create(@RequestBody User user) throws ValidationException {
        return userService.addUser(user);
    }

    @PutMapping("/users")
    public User update(@RequestBody User user) throws ObjectNotFoundException, ValidationException {
        return userService.updateUser(user);
    }

    @GetMapping("/users")
    public List<User> getList() {
        return userService.list();
    }

    @GetMapping("/users/{id}")
    public User getById(@PathVariable("id") Integer id) throws ObjectNotFoundException {
        return userService.getUserById(id);
    }

    @PutMapping("users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Integer userId,
                          @PathVariable("friendId") Integer friendId) throws ValidationException, ObjectNotFoundException {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Integer userId,
                             @PathVariable("friendId") Integer friendId) throws ValidationException, ObjectNotFoundException {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriendsList(@PathVariable("id") Integer userId) throws ObjectNotFoundException {
        return userService.getFriendsList(userId);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriendsList(@PathVariable("id") Integer userId,
                                           @PathVariable("otherId") Integer otherUserId) throws ValidationException, ObjectNotFoundException {
        return userService.getCommonFriendsList(userId, otherUserId);
    }

}
