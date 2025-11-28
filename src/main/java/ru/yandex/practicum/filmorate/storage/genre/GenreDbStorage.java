package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.stream.Collectors;

@Component("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbc;
    private final GenreRowMapper mapper;

    private static final String FIND_ALL_SQL =
            "SELECT * FROM genres ORDER BY id";

    private static final String FIND_BY_ID_SQL =
            "SELECT * FROM genres WHERE id = ?";

    private static final String FIND_BY_FILM_SQL =
            "SELECT g.* FROM film_genres fg " +
                    "JOIN genres g ON fg.genre_id = g.id " +
                    "WHERE fg.film_id = ? " +
                    "ORDER BY g.id";

    @Override
    public List<Genre> findAll() {
        return jdbc.query(FIND_ALL_SQL, mapper);
    }

    @Override
    public Optional<Genre> findById(Long id) {
        return jdbc.query(FIND_BY_ID_SQL, mapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public List<Genre> findByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        String placeholders = ids.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));

        String sql = """
                SELECT * FROM genres
                WHERE id IN (%s)
                ORDER BY id
                """.formatted(placeholders);

        return jdbc.query(sql, mapper, ids.toArray());
    }

    @Override
    public List<Genre> findGenresByFilmId(Long filmId) {
        return jdbc.query(FIND_BY_FILM_SQL, mapper, filmId);
    }

    @Override
    public Map<Long, List<Genre>> findGenresForFilms(Set<Long> filmIds) {
        if (filmIds.isEmpty()) return Map.of();

        String placeholders = filmIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));

        String sql = """
        SELECT fg.film_id, g.id, g.name
        FROM film_genres fg
        JOIN genres g ON g.id = fg.genre_id
        WHERE fg.film_id IN (%s)
        ORDER BY g.id
        """.formatted(placeholders);

        Map<Long, List<Genre>> result = new HashMap<>();

        jdbc.query(sql, rs -> {
            Long filmId = rs.getLong("film_id");
            Genre genre = new Genre(rs.getLong("id"), rs.getString("name"));

            result.computeIfAbsent(filmId, k -> new ArrayList<>()).add(genre);
        }, filmIds.toArray());

        return result;
    }

    public void updateGenresOfFilm(Long filmId, Set<Genre> genres) {
        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
        addGenresToFilm(filmId, genres);
    }

    public void addGenresToFilm(Long filmId, Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        genres.stream()
                .map(Genre::getId)
                .distinct()
                .forEach(id -> jdbc.update(sql, filmId, id));
    }
}
