package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override

    public Film findById(Long id) {
        String sql =
                "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATING_ID, r.NAME R_NAME " +
                        "FROM FILMS f JOIN RATINGS r ON f.RATING_ID = r.RATING_ID " +
                        "WHERE f.FILM_ID = ?";
        List<Film> result = jdbcTemplate.query(sql, this::mapToFilm, id);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    private Film mapToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("FILM_ID"));
        film.setName(resultSet.getString("NAME"));
        film.setDescription(resultSet.getString("DESCRIPTION"));
        film.setReleaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate());
        film.setDuration(resultSet.getInt("DURATION"));
        film.setMpa(new Rating(resultSet.getLong("RATING_ID"), resultSet.getString("R_NAME")));
        return film;
    }

    @Override
    public List<Film> findAll() {
        String sql =
                "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATING_ID, r.NAME R_NAME " +
                        "FROM FILMS f JOIN RATINGS r ON f.RATING_ID = r.RATING_ID ORDER BY f.FILM_ID";
        return jdbcTemplate.query(sql, this::mapToFilm);
    }

    @Override
    public List<Film> findPopular(int count) {
        String sql = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, " +
                "f.RATING_ID, r.NAME R_NAME, fl.COUNT FROM " +
                "(SELECT FILM_ID, COUNT(USER_ID) AS COUNT " +
                "FROM FILMS_LIKES fl " +
                "GROUP BY FILM_ID " +
                "ORDER BY COUNT(USER_ID) " +
                "LIMIT %s) fl " +
                "RIGHT JOIN FILMS f ON f.FILM_ID = fl.FILM_ID " +
                "JOIN RATINGS r ON f.RATING_ID = r.RATING_ID " +
                "ORDER BY fl.COUNT DESC, FILM_ID LIMIT %s ";
        String sqlString = String.format(sql, count, count);
        return jdbcTemplate.query(sqlString, this::mapToFilm);
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");

        Map<String, Object> values = new HashMap<>();
        values.put("NAME", film.getName());
        values.put("DESCRIPTION", film.getDescription());
        values.put("RELEASE_DATE", film.getReleaseDate());
        values.put("DURATION", film.getDuration());
        values.put("RATING_ID", film.getMpa().getId());

        film.setId(simpleJdbcInsert.executeAndReturnKey(values).longValue());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql =
                "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_ID = ? " +
                        "WHERE FILM_ID = ?";
        int update = jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        if (update == 0)
            throw new NotFoundException("Фильм не найден");
        return film;
    }

    @Override
    public void saveLikes(Film film) {
        jdbcTemplate.update("DELETE FROM FILMS_LIKES WHERE FILM_ID = ?", film.getId());

        String sql = "INSERT INTO FILMS_LIKES (FILM_ID, USER_ID) VALUES(?, ?)";
        Set<Long> likes = film.getLikes();
        for (var like : likes) {
            jdbcTemplate.update(sql, film.getId(), like);
        }
    }

    @Override
    public void loadLikes(Film film) {
        String sql = "SELECT USER_ID FROM FILMS_LIKES WHERE FILM_ID = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, film.getId());
        while (sqlRowSet.next()) {
            film.addLike(sqlRowSet.getLong("USER_ID"));
        }
    }


    @Override
    public void createGenresByFilm(Film film) {
        String sql = "INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES(?, ?)";
        Set<Genre> genres = film.getGenres();
        if (genres == null) {
            return;
        }
        for (var genre : genres) {
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        }
    }

    @Override
    public void updateGenresByFilm(Film film) {
        String sql = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, film.getId());
        createGenresByFilm(film);
    }
}
