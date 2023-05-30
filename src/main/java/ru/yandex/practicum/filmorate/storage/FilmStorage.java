package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FilmStorage extends CommonStorage<Film> {

    void createGenresByFilm(Film film);

    void updateGenresByFilm(Film film);

    void loadLikes(Film film);

    void saveLikes(Film film);

    List<Film> findPopular(int count);

    Map<Long, Set<Genre>> findGenresByIds(String ids);
    Map<Long, Set<Long>> findLikesByIds(String ids);
}
