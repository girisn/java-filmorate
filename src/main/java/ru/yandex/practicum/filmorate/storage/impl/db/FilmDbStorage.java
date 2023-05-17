package ru.yandex.practicum.filmorate.storage.impl.db;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

public class FilmDbStorage implements FilmStorage {
    @Override
    public Film add(Film film) throws ValidationException {
        return null;
    }

    @Override
    public Film update(Film film) throws ValidationException {
        return null;
    }

    @Override
    public List<Film> list() {
        return null;
    }

    @Override
    public boolean isExist(Integer id) {
        return false;
    }

    @Override
    public boolean contains(Film film) {
        return false;
    }

    @Override
    public boolean hasLike(Integer filmId, Integer userId) {
        return false;
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {

    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {

    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return null;
    }

    @Override
    public Film get(Integer filmId) {
        return null;
    }
}
