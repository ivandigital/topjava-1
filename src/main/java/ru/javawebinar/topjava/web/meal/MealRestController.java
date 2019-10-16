package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.assureUserMealConsistent;

@Controller
public class MealRestController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private MealService service;

    @Autowired
    public MealRestController(MealService service) {
        this.service = service;
    }

    public List<MealTo> getAll() {
        log.info("getAll");
        int userId = SecurityUtil.authUserId();
        return MealsUtil.getTos(service.getAll(userId), MealsUtil.DEFAULT_CALORIES_PER_DAY);
    }

    public MealTo get(int id) {
        log.info("get {}", id);
        int userId = SecurityUtil.authUserId();
        Meal meal = service.get(id, userId);
        return MealsUtil.createTo(meal);
    }

    public Meal create(MealTo meal) {
        log.info("create {}", meal);
        int userId = SecurityUtil.authUserId();
        Meal newMeal = new Meal(meal.getDateTime(), meal.getDescription(), meal.getCalories(), userId);
        return service.create(newMeal, userId);
    }

    public boolean update(int id, MealTo meal) {
        log.info("update {} with id={}", meal, id);
        int userId = SecurityUtil.authUserId();
        Meal newMeal = new Meal(id, meal.getDateTime(), meal.getDescription(), meal.getCalories(), userId);
        assureIdConsistent(newMeal, id);
        assureUserMealConsistent(userId, newMeal);
        return service.update(newMeal, userId) != null;
    }

    public void delete(int id) {
        int userId = SecurityUtil.authUserId();
        service.delete(id, userId);
    }

    public List<MealTo> findByParameters(Map<String, String[]> parameters) {
        int userId = SecurityUtil.authUserId();
        List<Meal> meals = service.getByParameters(parameters, userId) ;
        return MealsUtil.getTos(meals, SecurityUtil.authUserCaloriesPerDay());
    }

    public List<MealTo> getByPeriod(LocalDate dateStart, LocalDate dateEnd) {
        int userId = SecurityUtil.authUserId();
        return MealsUtil.getTos(service.getByPeriod(dateStart, dateEnd, userId), MealsUtil.DEFAULT_CALORIES_PER_DAY);
    }

    public List<MealTo> getByPeriod(LocalTime timeStart, LocalTime timeEnd) {
        int userId = SecurityUtil.authUserId();
        return MealsUtil.getTos(service.getByPeriod(timeStart, timeEnd, userId), MealsUtil.DEFAULT_CALORIES_PER_DAY);
    }

}