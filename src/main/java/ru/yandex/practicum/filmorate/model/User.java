package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.*;

@Data
public class User {
    private Integer id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Map<Integer, Boolean> friends = new HashMap<>();

    public void addFriend(User user) {
        if (this.friends.containsKey(user.id)) {
            return;
        }

        if (user.friends.containsKey(this.id)) {
            this.friends.put(user.id, true);
            user.friends.put(this.id, true);
        } else {
            this.friends.put(user.id, false);
        }
    }


}
