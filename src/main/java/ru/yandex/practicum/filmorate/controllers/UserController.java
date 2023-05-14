package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 1;

    @PostMapping("/users")
    public User create(@RequestBody User user) throws ValidationException {
        validate(user);
        if (users.containsKey(user.getId())) {
            log.info("Пользователь с id {} уже существует", user.getId());
            return null;
        }

        if (user.getLogin() != null && user.getName() == null) {
            user.setName(user.getLogin());
        }

        user.setId(id);
        users.put(id, user);
        id++;

        log.info("Создан объект: {}", user);
        return user;
    }

    @PutMapping("/users")
    public User update(@RequestBody User user) throws ValidationException {
        validate(user);
        if (!users.containsKey(user.getId())) {
            log.info("Пользователя с id {} не существует", user.getId());
            throw new ValidationException("Неизвестный id");
        }
        users.put(user.getId(), user);

        log.info("Обновлен объект: {}", user);
        return user;
    }

    @GetMapping("/users")
    public List<User> getList() {
        return new ArrayList<>(users.values());
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
