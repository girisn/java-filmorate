package ru.yandex.practicum.filmorate.model;

public enum Genre {
    DRAMA(1, "Драма"),
    COMEDY(2, "Комедия"),
    CARTOON(3, "Мультфильм"),
    DOCUMENTARY(4, "Документальный фильм"),
    THRILLER(5, "Триллер"),
    ACTION_MOVIE(6, "Боевик");

    private final String name;
    private final Integer id;

    private Genre(Integer id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }
}
