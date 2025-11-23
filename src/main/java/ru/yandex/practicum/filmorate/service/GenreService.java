package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage storage;

    public List<Genre> findAll() {
        return storage.findAll();
    }

    public Genre findById(Long id) {
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Жанр id=" + id + " не найден"));
    }
}
