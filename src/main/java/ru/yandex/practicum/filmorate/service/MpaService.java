package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaDbStorage storage;

    public List<MpaRating> findAll() {
        return storage.findAll();
    }

    public MpaRating findById(Long id) {
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException("MPA с id=" + id + " не найден"));
    }
}
