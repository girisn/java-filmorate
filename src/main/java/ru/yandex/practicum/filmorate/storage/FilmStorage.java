package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    List<Film> list();

    boolean isExist(Integer id);

    boolean contains(Film film);

    boolean hasLike(Integer filmId, Integer userId);

    void addLike(Integer filmId, Integer userId);

    void deleteLike(Integer filmId, Integer userId);

    List<Film> getPopularFilms(Integer count);

    Film getById(Integer filmId);
}
