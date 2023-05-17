package ru.yandex.practicum.filmorate.model;

public enum Genre {
    DRAMA("Драма"),
    COMEDY("Комедия"),
    CARTOON("Мультфильм"),
    DOCUMENTARY("Документальный фильм"),
    THRILLER("Триллер"),
    ACTION_MOVIE("Боевик");

    private final String name;

    private Genre(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
