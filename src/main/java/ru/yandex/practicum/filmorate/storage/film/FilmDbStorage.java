package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;
    private final FilmRowMapper mapper;
    private final GenreDbStorage genreDbStorage;

    private static final String ADD_LIKE_SQL =
            "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";

    private static final String REMOVE_LIKE_SQL =
            "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";

    private static final String FIND_LIKES_SQL =
            "SELECT user_id FROM film_likes WHERE film_id = ?";

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films(name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());
        genreDbStorage.addGenresToFilm(film.getId(), film.getGenres());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_id=? " +
                "WHERE id=?";

        int rows = jdbc.update(
                sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        if (rows == 0) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }

        genreDbStorage.updateGenresOfFilm(film.getId(), film.getGenres());
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        String sql = """
                SELECT f.*, m.name AS mpa_name
                FROM films f
                JOIN mpa_ratings m ON f.mpa_id = m.id
                """;

        List<Film> films = jdbc.query(sql, mapper);

        if (films.isEmpty()) {
            return films;
        }

        Set<Long> ids = films.stream()
                .map(Film::getId)
                .collect(Collectors.toSet());

        Map<Long, List<Genre>> genresByFilm = genreDbStorage.findGenresForFilms(ids);

        Map<Long, Set<Long>> likesByFilm = findLikesForFilms(ids);

        for (Film film : films) {
            film.setGenres(new LinkedHashSet<>(
                    genresByFilm.getOrDefault(film.getId(), List.of())
            ));

            film.setLikes(new HashSet<>(
                    likesByFilm.getOrDefault(film.getId(), Set.of())
            ));
        }

        return films;
    }

    @Override
    public List<Film> getPopular(int count) {
        String sql = """
                SELECT f.*, m.name AS mpa_name
                FROM films f
                LEFT JOIN film_likes fl ON f.id = fl.film_id
                JOIN mpa_ratings m ON f.mpa_id = m.id
                GROUP BY f.id
                ORDER BY COUNT(fl.user_id) DESC
                LIMIT ?
                """;

        List<Film> films = jdbc.query(sql, mapper, count);

        if (films.isEmpty()) {
            return films;
        }

        Set<Long> ids = films.stream()
                .map(Film::getId)
                .collect(Collectors.toSet());

        Map<Long, List<Genre>> genresByFilm = genreDbStorage.findGenresForFilms(ids);
        Map<Long, Set<Long>> likesByFilm = findLikesForFilms(ids);

        for (Film film : films) {
            film.setGenres(new LinkedHashSet<>(
                    genresByFilm.getOrDefault(film.getId(), List.of())
            ));
            film.setLikes(new HashSet<>(
                    likesByFilm.getOrDefault(film.getId(), Set.of())
            ));
        }

        return films;
    }

    @Override
    public Optional<Film> findById(Long id) {
        String sql = """
                SELECT f.*, m.name AS mpa_name
                FROM films f
                JOIN mpa_ratings m ON f.mpa_id = m.id
                WHERE f.id = ?
                """;

        return jdbc.query(sql, mapper, id)
                .stream()
                .findFirst()
                .map(film -> {
                    List<Genre> genres = genreDbStorage.findGenresByFilmId(id)
                            .stream()
                            .distinct()
                            .sorted(Comparator.comparingLong(Genre::getId))
                            .toList();

                    film.setGenres(new LinkedHashSet<>(genres));
                    film.getLikes().addAll(getLikes(id));
                    return film;
                });
    }

    private Map<Long, Set<Long>> findLikesForFilms(Set<Long> filmIds) {
        if (filmIds.isEmpty()) {
            return Map.of();
        }

        String placeholders = filmIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));

        String sql = """
                SELECT film_id, user_id
                FROM film_likes
                WHERE film_id IN (%s)
                """.formatted(placeholders);

        Map<Long, Set<Long>> result = new HashMap<>();

        jdbc.query(sql, rs -> {
            Long filmId = rs.getLong("film_id");
            Long userId = rs.getLong("user_id");
            result.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        }, filmIds.toArray());

        return result;
    }

    public void addLike(Long filmId, Long userId) {
        jdbc.update(ADD_LIKE_SQL, filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        jdbc.update(REMOVE_LIKE_SQL, filmId, userId);
    }

    public Set<Long> getLikes(Long filmId) {
        return new HashSet<>(jdbc.query(FIND_LIKES_SQL,
                (rs, rowNum) -> rs.getLong("user_id"),
                filmId
        ));
    }

}
