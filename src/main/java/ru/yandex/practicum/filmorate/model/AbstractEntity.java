package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public abstract class AbstractEntity {
    private Long id;

    public AbstractEntity(Long id) {
        this.id = id;
    }

    public AbstractEntity() {
    }
}