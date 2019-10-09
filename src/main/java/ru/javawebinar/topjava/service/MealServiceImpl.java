package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.List;

public class MealServiceImpl implements MealService {

    @Override
    public List<MealTo> findAll() {
        return MealsUtil.getAll();
    }

}
