package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    @Autowired
    private FilmStorage filmStorage;

    @Autowired
    private UserStorage userStorage;

    public Film addFilm(Film film) throws ValidationException {
        if (filmStorage.containsId(film.getId())) {
            log.info("Фильм с id {} уже существует", film.getId());
            return null;
        }

        validate(film);

        Film savedFilm = filmStorage.add(film);
        log.info("Создан объект: {}", film);

        return savedFilm;
    }

    public Film updateFilm(Film film) throws ValidationException, ObjectNotFoundException {
        if (!filmStorage.containsId(film.getId())) {
            log.info("Фильма с id {} не существует", film.getId());
            throw new ObjectNotFoundException("Неизвестный id");
        }

        validate(film);

        Film savedFilm = filmStorage.update(film);
        log.info("Обновлен объект: {}", film);

        return savedFilm;
    }

    public List<Film> list() {
        return filmStorage.list();
    }

    public Film getFilmById(Integer filmId) throws ObjectNotFoundException {
        if (!filmStorage.containsId(filmId)) {
            throw new ObjectNotFoundException("Неизвестный id");
        }
        return filmStorage.get(filmId);
    }

    public void addLike(Integer filmId, Integer userId) throws ObjectNotFoundException, ValidationException {
        validate(filmId, userId);
        if (filmStorage.hasLike(filmId, userId)) {
            throw new ValidationException("Лайк от пользователя уже существует");
        }
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) throws ObjectNotFoundException, ValidationException {
        validate(filmId, userId);
        if (!filmStorage.hasLike(filmId, userId)) {
            throw new ValidationException("Лайка от этого пользователя нет");
        }
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count);
    }

    private void validate(Integer filmId, Integer userId) throws ObjectNotFoundException {
        if (!filmStorage.containsId(filmId)) {
            throw new ObjectNotFoundException("Фильм с идентификатором " + filmId + " не найден");
        } else if (!userStorage.containsId(userId)) {
            throw new ObjectNotFoundException("Пользователь с идентификатором " + userId + " не найден");
        }
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
