package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    List<Genre> findAll();

    Optional<Genre> findById(Long id);

    List<Genre> findGenresByFilmId(Long filmId);

    List<Genre> findByIds(Set<Long> ids);

    Map<Long, List<Genre>> findGenresForFilms(Set<Long> filmIds);
}
