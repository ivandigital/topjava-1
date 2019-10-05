package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    private List<UserMeal> mealList;

    private UserMealsUtil() {
        initData();
    }

    private void initData() {
        mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,10,0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,13,0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,20,0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,10,0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,13,0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,20,0), "Ужин", 510)
        );
    }

    private void run(LocalTime startTime, LocalTime finishTime, int caloriesPerDay) {
        List<UserMealWithExceed> meals;

        System.out.println("Using for-each loop...");
        meals = getFilteredWithExceeded(mealList, startTime, finishTime, caloriesPerDay);
        printMealsWithExceed(meals);
    }

    public List<UserMealWithExceed> getFilteredWithExceeded(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // meals with calories exceed
        List<UserMealWithExceed> mealsOver = new ArrayList<>();

        // sum of calories up by day
        Map<LocalDate,Integer> totalCaloriesPerDay = new HashMap<>();

        int sumCalories = 0;
        for (UserMeal meal : mealList) {
            if (totalCaloriesPerDay.containsKey(meal.getDateTime().toLocalDate())) {
                sumCalories += meal.getCalories();
            }
            else {
                sumCalories = meal.getCalories();
            }
            totalCaloriesPerDay.put(meal.getDateTime().toLocalDate(), sumCalories);
        }

        // loop through meals and find meal in time and over calories per day
        for (UserMeal meal : mealList) {
            if (TimeUtil.isBetween(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                if (totalCaloriesPerDay.get(meal.getDateTime().toLocalDate()) > caloriesPerDay) {
                    mealsOver.add(new UserMealWithExceed(meal.getDateTime(), meal.getDescription(), meal.getCalories(), true));
                }
            }
        }

        return mealsOver;
    }

    private void printMealsWithExceed(List<UserMealWithExceed> mealList) {
        if (mealList.size() == 0) {
            System.out.println("No any user meal with over calories");
            return;
        }

        System.out.println("User meals with over calories:");

        for (UserMealWithExceed meal : mealList) {
            System.out.format("\tUser-meal = %s\n", meal);
        }
    }

    public static void main(String[] args) {
        UserMealsUtil app = new UserMealsUtil();
        app.run(LocalTime.of(7, 0), LocalTime.of(12,0), 2000);
    }

}
