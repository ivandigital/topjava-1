package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS.forEach(this::save);
    }

    @Override
    public Meal save(Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
            return meal;
        }
        // treat case: update, but not present in storage
        return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id) {
        return repository.remove(id) != null;
    }

    @Override
    public Meal get(int id) {
        return repository.get(id);
    }

    @Override
    public Collection<Meal> getAll() {
        return repository.values();
    }

    @Override
    public List<Meal> getAllByUser(int userId) {
        return filterMeals(meal -> meal.getUserId()==userId);
//        return repository.values().stream()
//                .filter(meal -> meal.getUserId()==userId)
//                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getByPeriod(LocalDate dateStart, LocalDate dateEnd, int userId) {
        return filterMeals(meal -> DateTimeUtil.isBetween(meal.getDate(), dateStart, dateEnd) && meal.getUserId()==userId);
//        return repository.values().stream()
//                .filter(meal -> DateTimeUtil.isBetween(meal.getDate(), dateStart, dateEnd))
//                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getByPeriod(LocalTime timeStart, LocalTime timeEnd, int userId) {
        return filterMeals(meal -> DateTimeUtil.isBetween(meal.getTime(), timeStart, timeEnd) && meal.getUserId()==userId);
//        return repository.values().stream()
//                .filter(meal -> DateTimeUtil.isBetween(meal.getTime(), timeStart, timeEnd))
//                .collect(Collectors.toList());
    }

    private List<Meal> filterMeals(Predicate<Meal> filter) {
        return repository.values().stream()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getTime))
                .sorted(Comparator.comparing(Meal::getDate)
                .reversed())
                .collect(Collectors.toList())
        ;
    }

}

