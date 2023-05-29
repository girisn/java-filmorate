package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component("userDb")
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User add(User user) {
        String query = "insert into users (email, login, name, birthday) values (?,?,?,?)";
        List<User> userList = jdbcTemplate.query(query, (rs, rowNum) -> makeUser(rs),
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        if (userList.size() != 0) {
            return userList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public User update(User user) {
        String query = "update users set email=?, login=?, name=?, birthday=? where id=?";
        List<User> userList = jdbcTemplate.query(query, (rs, rowNum) -> makeUser(rs),
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        if (userList.size() != 0) {
            return userList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<User> list() {
        String query = "select * from users";
        Collection<User> users = jdbcTemplate.query(query, (rs, rowNum) -> makeUser(rs));
        for (User user : users) {
            user.setFriends(getUserFriends(user.getId()));
        }
        return new ArrayList<>(users);
    }

    @Override
    public Optional<User> getById(Integer id) {
        String query = "select * from users where id = ?";
        Collection<User> users = jdbcTemplate.query(query, (rs, rowNum) -> makeUser(rs), id);
        if (users.size() == 0) {
            log.info("Пользователь с id = {} не найден", id);
            return Optional.empty();
        } else {
            log.info("Найден пользователь с id = {}", id);
            User user = users.iterator().next();
            user.setFriends(getUserFriends(user.getId()));
            return Optional.of(user);
        }
    }

    @Override
    public Optional<User> getFriendById(Integer userId, Integer friendId) {
        return Optional.empty();
    }

    private User makeUser(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        return new User(id, email, login, name, birthday);
    }

    private Map<Integer, Boolean> getUserFriends(Integer userId) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select * from friends where user_id = ?", userId);
        Map<Integer, Boolean> friends = new HashMap<>();
        while (rowSet.next()) {
            friends.put(rowSet.getInt("friend_id"), false); //todo
        }
        return friends;
    }



    @Override
    public void addFriend(Integer userId, Integer friendId) {

    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {

    }

    @Override
    public List<User> friendsList(Integer userId) {
        return null;
    }

    @Override
    public List<User> commonFriendsList(Integer userId, Integer otherUserId) {
        return null;
    }
}
