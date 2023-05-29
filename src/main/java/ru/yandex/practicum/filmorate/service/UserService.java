package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    @Autowired
    @Qualifier("userDb")
    private UserStorage userStorage;

    public User add(User user) throws ValidationException {
        Optional<User> fromDb = userStorage.getById(user.getId());
        if (fromDb.isEmpty()) {
            log.info("Пользователь с id {} уже существует", user.getId());
            return null;
        }

        validate(user);

        if (user.getLogin() != null && !StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }

        User savedUser = userStorage.add(user);
        log.info("Создан объект: {}", user);

        return savedUser;
    }

    public User update(User user) throws ValidationException, ObjectNotFoundException {
        Optional<User> fromDb = userStorage.getById(user.getId());
        if (fromDb.isEmpty()) {
            log.info("Пользователя с id {} не существует", user.getId());
            throw new ObjectNotFoundException("Неизвестный id");
        }
        validate(user);

        User savedUser = userStorage.update(user);
        log.info("Обновлен объект: {}", user);

        return savedUser;
    }

    public List<User> list() {
        return userStorage.list();
    }

    public User getById(Integer id) throws ObjectNotFoundException {
        Optional<User> user = userStorage.getById(id);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("Пользователь с идентификатором " + id + " не найден");
        }
        return user.get();
    }

    public void addFriend(Integer userId, Integer friendId) throws ObjectNotFoundException, ValidationException {
        validate(userId, friendId);

        if (Objects.equals(userId, friendId)) {
            throw new ValidationException("Пользователь не может добавить в друзья сам себя");
        } else {
            Optional<User> friend = userStorage.getFriendById(userId, friendId);
            if (friend.isEmpty()) {
                throw new ValidationException("Пользователи уже друг у друга в друзьях");
            }
        }
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) throws ValidationException, ObjectNotFoundException {
        validate(userId, friendId);

        if (Objects.equals(userId, friendId)) {
            throw new ValidationException("Пользователь не может удалить из друзей сам себя");
        } else {
            Optional<User> friend = userStorage.getFriendById(userId, friendId);
            if (friend.isEmpty()) {
                throw new ValidationException("Пользователей нет друг у друга в друзьях");
            }
        }

        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriendsList(Integer userId) throws ObjectNotFoundException {
        Optional<User> user = userStorage.getById(userId);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("Пользователь с идентификатором " + userId + " не найден");
        }
        return userStorage.friendsList(userId);
    }

    public List<User> getCommonFriendsList(Integer userId, Integer otherUserId) throws ObjectNotFoundException, ValidationException {
        validate(userId, otherUserId);
        if (Objects.equals(userId, otherUserId)) {
            throw new ValidationException("Идентификаторы должны различаться");
        }
        return userStorage.commonFriendsList(userId, otherUserId);
    }

    private void validate(Integer userId, Integer friendId) throws ObjectNotFoundException {
        Optional<User> user = userStorage.getById(userId);
        Optional<User> friend = userStorage.getById(friendId);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("Пользователь с идентификатором " + userId + " не найден");
        } else if (friend.isEmpty()) {
            throw new ObjectNotFoundException("Пользователь с идентификатором " + friendId + " не найден");
        }
    }

    private void validate(User user) throws ValidationException {
        if (user == null) {
            throw new ValidationException("Тело запроса не может быть пустым");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Поле email не может быть пустым");
        } else if (!user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный формат поля email");
        } else if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Поле login не может быть пустым");
        } else if (user.getLogin().contains(" ")) {
            throw new ValidationException("Поле login не может содержать пробелы");
        } else if (user.getBirthday() != null && user.getBirthday().compareTo(LocalDate.now()) > 0) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
