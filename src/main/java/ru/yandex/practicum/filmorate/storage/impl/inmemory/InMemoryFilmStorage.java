package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id = 1;

    @Override
    public Film add(Film film) throws ValidationException {
        film.setId(id);
        films.put(film.getId(), film);
        id++;

        return film;
    }

    @Override
    public Film update(Film film) throws ValidationException {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> list() {
        return new ArrayList<>(films.values());
    }

    @Override
    public boolean isExist(Integer id) {
        return films.containsKey(id);
    }

    @Override
    public boolean hasLike(Integer filmId, Integer userId) {
        return films.get(filmId).getLikes().contains(userId);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        Film film = films.get(filmId);
        film.getLikes().add(userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        Film film = films.get(filmId);
        film.getLikes().remove(userId);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        Comparator<Film> comparator = new Comparator<Film>() {
            @Override
            public int compare(Film o1, Film o2) {
                return Integer.compare(o2.getLikes().size(), o1.getLikes().size());
            }
        };
        return films.values().stream().sorted(comparator).limit(count).collect(Collectors.toList());
    }

    @Override
    public Film get(Integer filmId) {
        return films.get(filmId);
    }

    @Override
    public boolean contains(Film film) {
        return films.containsKey(film.getId());
    }

}
