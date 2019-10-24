package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    private static final int USER_ID = UserTestData.USER_ID;
    private static final int ADMIN_ID = UserTestData.ADMIN_ID;
    private static final int USER_MEAL_ID = 100_005;
    private static final int NONE_MEAL_ID = 111;

    @Autowired
    private MealService service;

    @Test
    public void get() {
        int mealId = USER_MEAL_ID;
        Meal meal = service.get(mealId, USER_ID);
        assertNotNull(meal);
        assertEquals(meal.getId().intValue(), mealId);
    }

    @Test(expected = NotFoundException.class)
    public void getNotFound() throws Exception {
        service.get(NONE_MEAL_ID, USER_ID);
    }

    @Test(expected = NotFoundException.class)
    public void getStrangeMeal() {
        service.get(USER_MEAL_ID, ADMIN_ID);
    }

    @Test
    public void getBetweenDateInterval() {
        LocalDate dateStart = LocalDate.of(2019, 10, 22);
        LocalDate dateEnd = LocalDate.of(2019, 10, 23);
        List<Meal> meals = service.getBetweenDates(dateStart, dateEnd, USER_ID);
        for (Meal m : meals) {
            assertTrue(m.getDate().isAfter(dateStart) || m.getDate().isEqual(dateStart));
            assertTrue(m.getDate().isBefore(dateEnd) || m.getDate().isEqual(dateEnd));
        }
    }

    @Test
    public void getAfterDate() {
        LocalDate dateStart = LocalDate.of(2019, 10, 22);
        List<Meal> meals = service.getBetweenDates(dateStart, null, USER_ID);
        for (Meal m : meals) {
            assertTrue(m.getDate().isAfter(dateStart) || m.getDate().isEqual(dateStart));
        }
    }

    @Test
    public void getBeforeDate() {
        LocalDate dateEnd = LocalDate.of(2019, 10, 22);
        List<Meal> meals = service.getBetweenDates(null, dateEnd, USER_ID);
        for (Meal m : meals) {
            assertTrue(m.getDate().isBefore(dateEnd) || m.getDate().isEqual(dateEnd));
        }
    }

    public void getBetweenTimeInterval() {
        LocalTime timeStart = LocalTime.of(10, 12);
        LocalTime timeEnd = LocalTime.of(17, 40);
        List<Meal> meals = service.getBetweenTimes(timeStart, timeEnd, USER_ID);
        for (Meal m : meals) {
            assertTrue(m.getTime().isAfter(timeStart) || m.getTime().equals(timeStart));
            assertTrue(m.getTime().isBefore(timeEnd) || m.getTime().equals(timeEnd));
        }
    }

    @Test
    public void getAll() {
        List<Meal> meals = service.getAll(USER_ID);
        assertNotNull(meals);
        assertTrue(meals.size() > 0);
    }

    @Test
    public void create() {
        LocalDateTime dt = LocalDateTime.of(2019, 10, 25, 14, 30);
        Meal newMeal = new Meal(dt, "Хавчик", 555);
        Meal savedMeal = service.create(newMeal, USER_ID);
        assertNotNull(savedMeal);
        assertNotNull(savedMeal.getId());
        assertTrue(savedMeal.getId() > 0);
        Meal getMeal = service.get(savedMeal.getId().intValue(), USER_ID);
        assertNotNull(getMeal);
    }

    @Test
    public void update() {
        String desc = "Test meal";
        int cal = 777;
        Meal meal = service.get(USER_MEAL_ID, USER_ID);
        assertNotNull(meal);
        meal.setDescription(desc);
        meal.setCalories(cal);
        service.update(meal, USER_ID);
        Meal updMeal = service.get(USER_MEAL_ID, USER_ID);
        assertNotNull(updMeal);
        assertEquals(desc, updMeal.getDescription());
        assertEquals(cal, updMeal.getCalories());
    }

    @Test(expected = NotFoundException.class)
    public void updateStrangeMeal() {
        Meal meal = service.get(USER_MEAL_ID, USER_ID);
        meal.setDescription("Not my meal");
        meal.setCalories(123456);
        service.update(meal, ADMIN_ID);
    }

    @Test(expected = NotFoundException.class)
    public void delete() {
        int mealId = USER_MEAL_ID;
        service.delete(mealId, USER_ID);
        Meal deletedMeal = service.get(mealId, USER_ID);
        assertNull(deletedMeal);
    }

    @Test(expected = NotFoundException.class)
    public void deleteStrangeMeal() {
        int mealId = USER_MEAL_ID;
        service.delete(mealId, ADMIN_ID);
        Meal meal = service.get(mealId, USER_ID);
        assertNotNull(meal);
    }

}