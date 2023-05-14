package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserStorage userStorage;

    public User addUser(User user) throws ValidationException {
        if (userStorage.containsId(user.getId())) {
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

    public User updateUser(User user) throws ValidationException, ObjectNotFoundException {
        if (!userStorage.containsId(user.getId())) {
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

    public User getUserById(Integer id) throws ObjectNotFoundException {
        if (!userStorage.containsId(id)) {
            throw new ObjectNotFoundException("Пользователь с идентификатором " + id + " не найден");
        }
        return userStorage.get(id);
    }

    public void addFriend(Integer userId, Integer friendId) throws ObjectNotFoundException, ValidationException {
        validate(userId, friendId);

        if (Objects.equals(userId, friendId)) {
            throw new ValidationException("Пользователь не может добавить в друзья сам себя");
        } else if (userStorage.userHasFriend(userId, friendId)) {
            throw new ValidationException("Пользователи уже друг у друга в друзьях");
        }
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) throws ValidationException, ObjectNotFoundException {
        validate(userId, friendId);

        if (Objects.equals(userId, friendId)) {
            throw new ValidationException("Пользователь не может удалить из друзей сам себя");
        } else if (!userStorage.userHasFriend(userId, friendId)) {
            throw new ValidationException("Пользователей нет друг у друга в друзьях");
        }

        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriendsList(Integer userId) throws ObjectNotFoundException {
        if (!userStorage.containsId(userId)) {
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
        if (!userStorage.containsId(userId)) {
            throw new ObjectNotFoundException("Пользователь с идентификатором " + userId + " не найден");
        } else if (!userStorage.containsId(friendId)) {
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
