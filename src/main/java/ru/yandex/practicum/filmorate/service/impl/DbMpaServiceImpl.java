package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DbMpaServiceImpl implements MpaService {
    private final MpaStorage mpaStorage;

    @Override
    public List<Mpa> getMpas() {
        return mpaStorage.findAll();
    }

    @Override
    public Mpa getMpa(Integer id) {
        return mpaStorage.getById(id).orElseThrow(() ->
                new MpaNotFoundException(String.format(
                        "Рейтинг с ID %s не найден", id)));
    }
}
