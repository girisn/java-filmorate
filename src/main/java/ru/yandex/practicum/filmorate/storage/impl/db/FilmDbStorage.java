package ru.yandex.practicum.filmorate.storage.impl.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component("filmDb")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film add(Film film) {
        String query = "insert into films (name, description, release_date) values (?,?,?)";
        List<Film> filmList = jdbcTemplate.query(query, (rs, rowNum) -> makeFilm(rs),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate());
        if (filmList.size() != 0) {
            return filmList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Film update(Film film) {
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
    public Film getById(Integer filmId) {
        return null;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("releaseDate").toLocalDate();
        Integer duration = rs.getInt("duration");
        Integer genreId = rs.getInt("genre_id");
        Integer ratingId = rs.getInt("rating_id");

        Film film = new Film();
        film.setId(id);
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);

        Optional<Genre> genre = getGenreById(genreId);
        Optional<Rating> rating = getRatingById(ratingId);
        Set<Integer> likes = getLikesByFilmId(id);

        film.setGenre(genre.orElse(null));
        film.setRating(rating.orElse(null));
        film.setLikes(likes);

        return film;
    }

    private Set<Integer> getLikesByFilmId(Integer filmId) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select * from likes where film_id = ?", filmId);
        Set<Integer> likes = new HashSet<>();
        while (rowSet.next()) {
            likes.add(rowSet.getInt("user_id"));
        }
        return likes;
    }

    private Optional<Genre> getGenreById(Integer genreId) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select * from genres where id = ?", genreId);
        if (rowSet.next()) {
            Genre genre = Genre.valueOf(rowSet.getString("name"));
            return Optional.of(genre);
        } else {
            return Optional.empty();
        }
    }

    private Optional<Rating> getRatingById(Integer ratingId) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select * from ratings where id = ?", ratingId);
        if (rowSet.next()) {
            Rating rating = Rating.valueOf(rowSet.getString("name"));
            return Optional.of(rating);
        } else {
            return Optional.empty();
        }
    }
}
