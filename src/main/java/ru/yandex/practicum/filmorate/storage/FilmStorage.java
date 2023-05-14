package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film add(Film film) throws ValidationException;

    Film update(Film film) throws ValidationException;

    List<Film> list();

    boolean containsId(Integer id);

    boolean hasLike(Integer filmId, Integer userId);

    void addLike(Integer filmId, Integer userId);

    void deleteLike(Integer filmId, Integer userId);

    List<Film> getPopularFilms(Integer count);

    Film get(Integer filmId);
}
