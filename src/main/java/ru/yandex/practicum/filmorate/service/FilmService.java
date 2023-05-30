package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InvalidFilmException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService extends AbstractService<Film, FilmStorage> {
    private static final String MSG_ERR_DATE = "Дата релиза не раньше 28 декабря 1895 года ";
    private static final String MSG_ERR_MPA = "Не заполнен рейтинг MPA";

    private LocalDate minimumDate = LocalDate.of(1895, 12, 28);
    private final UserService userService;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmService(FilmStorage storage, UserService userService, GenreStorage genreStorage) {
        super(storage);
        this.userService = userService;
        this.genreStorage = genreStorage;
    }

    @Override
    public Film create(Film film) {
        film = super.create(film);
        storage.createGenresByFilm(film);
        log.info("Добавлен фильма {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        film = super.update(film);
        storage.updateGenresByFilm(film);
        log.info("Обновлён фильм {}", film);
        return film;
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = super.findAll();
        this.loadDataForlist(films);
//        films.forEach(this::loadData);
        return films;
    }

    @Override
    public Film findById(Long id) {
        Film film = super.findById(id);
        loadData(film);
        return film;
    }

    private void loadData(Film film) {
        film.setGenres(genreStorage.getGenresByFilm(film));
        storage.loadLikes(film);
    }

    private void loadDataForlist(List<Film> films) {
        List<String> ids = films.stream().map(film -> film.getId().toString()).collect(Collectors.toList());
        String idsString = StringUtils.join(ids, ',');
        Map<Long, Set<Genre>> mapGenres = storage.findGenresByIds(idsString);
        Map<Long, Set<Long>> mapLikes = storage.findLikesByIds(idsString);
        for (Film film : films) {
            film.setGenres(mapGenres.getOrDefault(film.getId(), Collections.emptySet()));
            if (mapLikes.containsKey(film.getId())) {
                for (Long like : mapLikes.get(film.getId())) {
                    film.addLike(like);
                }
            }
        }
    }

    @Override
    public void validationBeforeCreate(Film film) {
        validateReleaseDate(film.getReleaseDate());
        validateMpa(film.getMpa());
    }

    @Override
    public void validationBeforeUpdate(Film film) {
        super.validationBeforeUpdate(film);
        validateReleaseDate(film.getReleaseDate());
        validateMpa(film.getMpa());
    }

    private void validateReleaseDate(LocalDate date) {
        if (date.isBefore(minimumDate)) {
            log.warn(MSG_ERR_DATE + date);
            throw new InvalidFilmException(MSG_ERR_DATE);
        }
    }

    private void validateMpa(Rating rating) {
        if (rating == null) {
            log.warn(MSG_ERR_MPA);
            throw new InvalidFilmException(MSG_ERR_MPA);
        }
    }

    private void validateLike(Film film, User user) {
        if (film == null) {
            String message = ("Фильм не найден");
            log.warn(message);
            throw new NotFoundException(message);
        }
        if (user == null) {
            String message = ("Пользователь не найден");
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    public void addLike(Long id, Long userId) {
        Film film = this.findById(id);
        User user = userService.findById(userId);
        validateLike(film, user);
        film.addLike(userId);
        storage.saveLikes(film);
    }

    public void removeLike(Long id, Long userId) {
        Film film = this.findById(id);
        User user = userService.findById(userId);
        validateLike(film, user);
        film.removeLike(userId);
        storage.saveLikes(film);
    }

    public List<Film> findPopularMovies(int count) {
        List<Film> films = this.storage.findPopular(count);
        this.loadDataForlist(films);
//        films.forEach(this::loadData);
        return films;
    }
}