INSERT INTO genres (name)
VALUES ('Драма'),
    ('Комедия'),
    ('Мультфильм'),
    ('Документальный фильм'),
    ('Триллер'),
    ('Боевик')
ON CONFLICT DO NOTHING;

INSERT INTO ratings (name)
VALUES ('G'),
    ('PG'),
    ('PG-13'),
    ('R'),
    ('NC-17')
ON CONFLICT DO NOTHING;