package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id = 1;

    @GetMapping("/films")
    public List<Film> getList() {
        return new ArrayList<>(films.values());
    }

    @PostMapping("/films")
    public Film create(@RequestBody Film film) throws ValidationException {
        validate(film);
        if (films.containsKey(film.getId())) {
            log.info("Фильм с id {} уже существует", film.getId());
            return null;
        }
        film.setId(id);
        log.info("Создан объект: {}", film);
        films.put(film.getId(), film);
        id++;
        return film;
    }

    @PutMapping("/films")
    public Film update(@RequestBody Film film) throws ValidationException {
        validate(film);
        if (!films.containsKey(film.getId())) {
            log.info("Фильма с id {} не существует", film.getId());
            throw new ValidationException("Неизвестный id");
        }

        films.put(film.getId(), film);
        log.info("Обновлен объект: {}", film);
        return film;
    }

    private void validate(Film film) throws ValidationException {
        if (film == null) {
            throw new ValidationException("Тело запроса не может быть пустым");
        }

        if (!StringUtils.hasText(film.getName())) {
            throw new ValidationException("Название фильма не может быть пустым");
        } else if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания - 200 символов");
        } else if (film.getReleaseDate().compareTo(LocalDate.of(1895, 12, 28)) < 0) {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        } else if (film.getDuration() != null && film.getDuration() < 0) {
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }
    }
}
