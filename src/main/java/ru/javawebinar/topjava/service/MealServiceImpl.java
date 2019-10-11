package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.data.MealDataProvider;
import ru.javawebinar.topjava.model.MealTo;

import java.util.List;

public class MealServiceImpl implements MealService {

    MealDataProvider dataProvider = MealDataProvider.getInstance();

    @Override
    public List<MealTo> findAll() {
        return dataProvider.findAll();
    }

    @Override
    public MealTo get(int id) {
        return dataProvider.findById(id);
    }

}
