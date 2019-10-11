package ru.javawebinar.topjava.data;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.swing.plaf.IconUIResource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MealDataProvider {

    private static volatile MealDataProvider instance;

    private static List<Meal> meals;

    private MealDataProvider() {
        initData();
    };

    private static void initData() {
        meals = Arrays.asList(
                new Meal(1, LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500),
                new Meal(2, LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000),
                new Meal(3, LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500),
                new Meal(4, LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000),
                new Meal(5, LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500),
                new Meal(6, LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510)
        );
    }

    public static MealDataProvider getInstance() {
        if (instance == null) {
            synchronized (MealDataProvider.class) {
                if (instance == null)
                    instance = new MealDataProvider();
                //end if
            }
        }
        return instance;
    }

    public static List<Meal> getData() {
        return meals;
    }

    public static List<MealTo> findAll() {
        return MealsUtil.getFiltered(meals, LocalTime.MIN, LocalTime.MAX, MealsUtil.DEFAULT_CALORIES_PER_DAY);
    }

    public static MealTo findById(int id) {
        Meal meal = meals.stream().filter(m -> m.getId()==id).findFirst().orElse(null);
        if (meal != null) {
            return new MealTo(meal.getId(), meal.getDateTime(), meal.getDescription(), meal.getCalories(), false);
        }
        return null;
    }

    public static int add(LocalDateTime dateTime, String description, int calories) {
        int counter = 0;
        synchronized (meals) {
            counter = getMaxId();
            meals.add(new Meal(counter+1, dateTime, description, calories));
        }
        return counter;
    }

    public static boolean remove(int id) {
        synchronized (meals) {
            Meal meal = meals.stream().filter(m -> m.getId()==id).findFirst().get();
            if (meal != null) {
                meals.remove(meal);
                return false;
            }
            return false;
        }
    }

    private static int getMaxId() {
        return meals.stream().max(Comparator.comparing(Meal::getId)).get().getId();
    }
}
