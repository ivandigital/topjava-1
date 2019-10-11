package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.data.MealDataProvider;
import ru.javawebinar.topjava.model.MealTo;

import java.util.List;

public class MealServiceImpl implements MealService {

    @Override
    public List<MealTo> findAll() {
        return MealDataProvider.getInstance().findAll();
    }

}
