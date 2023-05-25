package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class DbMpaServiceImpl implements MpaService {
    private final MpaStorage mpaStorage;

    public DbMpaServiceImpl(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @Override
    public List<Mpa> getMpas() {
        return mpaStorage.getMpas().orElse(new ArrayList<>());
    }

    @Override
    public Mpa getMpa(Integer id) {
        return mpaStorage.getMpa(id).orElseThrow(() ->
                new MpaNotFoundException(String.format(
                        "Рейтинг с ID %s не найден", id)));
    }
}
