package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.MealTo;

import java.util.List;

public interface MealService {

    public List<MealTo> findAll();

    public MealTo get(int id);

}
