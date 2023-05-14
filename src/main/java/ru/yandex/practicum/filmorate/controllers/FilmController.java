package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
public class FilmController {
    @Autowired
    private FilmService filmService;

    @GetMapping("/films")
    public List<Film> getList() {
        return filmService.list();
    }

    @PostMapping("/films")
    public Film create(@RequestBody Film film) throws ValidationException {
        return filmService.addFilm(film);
    }

    @PutMapping("/films")
    public Film update(@RequestBody Film film) throws ValidationException, ObjectNotFoundException {
        return filmService.updateFilm(film);
    }

    @GetMapping("/films/{id}")
    public Film getById(@PathVariable("id") Integer filmId) throws ObjectNotFoundException {
        return filmService.getFilmById(filmId);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer filmId,
                        @PathVariable("userId") Integer userId) throws ValidationException, ObjectNotFoundException {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer filmId,
                           @PathVariable("userId") Integer userId) throws ValidationException, ObjectNotFoundException {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", required = false) Integer count) {
        if (count == null) {
            count = 10;
        }
        return filmService.getPopularFilms(count);
    }
}
