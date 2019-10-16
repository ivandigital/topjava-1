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
import java.util.stream.Stream;

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
    }

    @Override
    public List<Meal> getByPeriod(LocalDate dateStart, LocalDate dateEnd, int userId) {
        return filterMeals(meal -> DateTimeUtil.isBetween(meal.getDate(), dateStart, dateEnd) && meal.getUserId()==userId);
    }

    @Override
    public List<Meal> getByPeriod(LocalTime timeStart, LocalTime timeEnd, int userId) {
        return filterMeals(meal -> DateTimeUtil.isBetween(meal.getTime(), timeStart, timeEnd) && meal.getUserId()==userId);
    }

    @Override
    public List<Meal> getByParameters(Map<String, String[]> parameters, int userId) {
        Stream<Meal> mealStream = repository.values().stream();

        // date interval
        mealStream = filterBetweenDate(mealStream, parameters, "dateBegin", "dateEnd");

        // time interval
        mealStream = filterBetweenTime(mealStream, parameters, "timeBegin", "timeEnd");
/*
        // date-begin
        if (parameters.get("dateBegin") != null && parameters.get("dateBegin").length > 0) {
            LocalDate filterDate = LocalDate.parse(parameters.get("dateBegin")[0]);
            mealStream = mealStream.filter(meal -> meal.getDate().compareTo(filterDate) >= 0);
        }
*/
        // Sorting
        mealStream = mealStream
                        .sorted(Comparator.comparing(Meal::getTime))
                        .sorted(Comparator.comparing(Meal::getDate)
                        .reversed());

        return mealStream.collect(Collectors.toList());
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

    private static Stream<Meal> filterBetweenDate(Stream<Meal> stream, Map<String, String[]> parameters, String paramDateStart, String paramDateEnd) {
        if (parameters == null || parameters.isEmpty())
            return stream;
        //end if
        String strDateStart = getParameterValue(parameters, paramDateStart);
        String strDateEnd = getParameterValue(parameters, paramDateEnd);
        if (strDateStart != null && !strDateStart.trim().isEmpty()) {
            LocalDate filterDate = LocalDate.parse(strDateStart.trim());
            stream = stream.filter(meal -> meal.getDate().compareTo(filterDate) >= 0);
        }
        if (strDateEnd != null && !strDateEnd.trim().isEmpty()) {
            LocalDate filterDate = LocalDate.parse(strDateEnd.trim());
            stream = stream.filter(meal -> meal.getDate().compareTo(filterDate) <= 0);
        }
        return stream;
    }

    private static Stream<Meal> filterBetweenTime(Stream<Meal> stream, Map<String, String[]> parameters, String paramTimeStart, String paramTimeEnd) {
        if (parameters == null || parameters.isEmpty())
            return stream;
        //end if
        String strTimeStart = getParameterValue(parameters, paramTimeStart);
        String strTimeEnd = getParameterValue(parameters, paramTimeEnd);
        if (strTimeStart != null && !strTimeStart.trim().isEmpty()) {
            LocalTime filterTime = LocalTime.parse(strTimeStart.trim());
            stream = stream.filter(meal -> meal.getTime().compareTo(filterTime) >= 0);
        }
        if (strTimeEnd != null && !strTimeEnd.trim().isEmpty()) {
            LocalTime filterTime = LocalTime.parse(strTimeEnd.trim());
            stream = stream.filter(meal -> meal.getTime().compareTo(filterTime) <= 0);
        }
        return stream;
    }

    private static String getParameterValue(Map<String, String[]> parameters, String paramName) {
        if (parameters == null || parameters.isEmpty())
            return null;
        //end if
        if (parameters.get(paramName) == null || parameters.get(paramName).length == 0)
            return null;
        //end if
        return parameters.get(paramName)[0];
    }

}

