package ru.javawebinar.topjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.*;

@Service
public class MealService {

    private final MealRepository repository;

    @Autowired
    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal create(Meal meal, int userId) {
        checkNew(meal);
        assureUserMealConsistent(userId, meal);
        return repository.save(meal);
    }

    public Meal get(int id, int userId) {
        Meal meal = repository.get(id);
        assureUserMealConsistent(userId, meal);
        return meal;
///        return checkNotFoundWithId(repository.get(id), id);
    }

    public Meal update(Meal meal, int userId) {
        checkNotNew(meal);
        checkNotFoundWithId(repository.get(meal.getId()), meal.getId());
        assureUserMealConsistent(userId, meal);
        return repository.save(meal);
    }

    public void delete(int id, int userId) {
        Meal meal = repository.get(id);
        assureUserMealConsistent(userId, meal);
        repository.delete(id);
///        checkNotFoundWithId(repository.delete(id), id);
    }

    public List<Meal> getAll(int userId) {
        return repository.getAllByUser(userId);
    }

    public List<Meal> getByPeriod(LocalDate dateStart, LocalDate dateEnd, int userId) {
        return repository.getByPeriod(dateStart, dateEnd, userId);
    }

    public List<Meal> getByPeriod(LocalTime timeStart, LocalTime timeEnd, int userId) {
        return repository.getByPeriod(timeStart, timeEnd, userId);
    }

}