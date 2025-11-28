package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbc;
    private final MpaRowMapper mapper;

    private static final String FIND_ALL_SQL =
            "SELECT * FROM mpa_ratings ORDER BY id";

    private static final String FIND_BY_ID_SQL =
            "SELECT * FROM mpa_ratings WHERE id = ?";

    @Override
    public List<MpaRating> findAll() {
        return jdbc.query(FIND_ALL_SQL, mapper);
    }

    @Override
    public Optional<MpaRating> findById(Long id) {
        return jdbc.query(FIND_BY_ID_SQL, mapper, id)
                .stream()
                .findFirst();
    }
}
