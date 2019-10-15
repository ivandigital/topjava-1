package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

public interface MealRepository {
    // null if not found, when updated
    Meal save(Meal meal);

    // false if not found
    boolean delete(int id);

    // null if not found
    Meal get(int id);

    // get all meals
    Collection<Meal> getAll();

    List<Meal> getAllByUser(int userId);

    // get meals in date peridd
    List<Meal> getByPeriod(LocalDate dateStart, LocalDate dateEnd, int userId);

    // get meals in time period
    List<Meal> getByPeriod(LocalTime timeStart, LocalTime timeEnd, int userId);

}
