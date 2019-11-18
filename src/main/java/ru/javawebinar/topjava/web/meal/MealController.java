package ru.javawebinar.topjava.web.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
@RequestMapping(value = "/meals")
public class MealController {

    private final static String PAGE_LIST = "meals";
    private final static String PAGE_FORM = "mealForm";

    @Autowired
    private MealService service;

    @RequestMapping(method = RequestMethod.GET)
    public String getAll(Model model) {
        List<Meal> meals = service.getAll(getAuthUserId());
        model.addAttribute("meals", MealsUtil.getTos(meals, SecurityUtil.authUserCaloriesPerDay()));
        return PAGE_LIST;
    }

    @RequestMapping(params = "action=update", method = RequestMethod.GET)
    public String getForUpdate(@RequestParam("id") Integer id, Model model) {
        Meal meal;
        if (id != null) {
            meal = service.get(id, getAuthUserId());
        }
        else {
            meal = createMealStub();
        }
        if (meal != null) {
            model.addAttribute("meal", meal);
            return PAGE_FORM;
        }
        else {
            return "404";
        }
    }

    @RequestMapping(params = "action=create", method = RequestMethod.GET)
    public String getForCreate(Model model) {
        return getForUpdate(null, model);
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String create(HttpServletRequest request) {
        Meal meal = new Meal();
        meal.setDescription(request.getParameter("description"));
        meal.setCalories(Integer.parseInt(request.getParameter("calories")));
        meal.setDateTime(LocalDateTime.parse(request.getParameter("datetime")));
        meal = service.create(meal, getAuthUserId());
        if (meal != null) {
            return PAGE_LIST;
        }
        else {
            return "404";
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public String save(WebRequest request) {
        Meal meal = new Meal();
        meal.setDescription(request.getParameter("description"));
        if (! StringUtils.isEmpty(request.getParameter("calories"))) {
            meal.setCalories(Integer.parseInt(request.getParameter("calories")));
        }
        if (! StringUtils.isEmpty(request.getParameter("dateTime"))) {
            meal.setDateTime(LocalDateTime.parse(request.getParameter("dateTime")));
        }
        String id = request.getParameter("id");
        if (id == null || id.equals("")) { // create
            service.create(meal, getAuthUserId());
        }
        else { // update
            meal.setId(Integer.parseInt(id));
            service.update(meal, getAuthUserId());
        }
        return "redirect:" + PAGE_LIST;
    }

    @RequestMapping(params = {"action=delete", "id"}, method = RequestMethod.GET)
    public String deleteByAction(@RequestParam int id) {
        return delete(id);
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.GET)
    public String delete(@PathVariable int id) {
        service.delete(id, getAuthUserId());
        return "redirect:" + PAGE_LIST;
    }

    @RequestMapping(params = {"action=filter", "startDate", "endDate"}, method = RequestMethod.GET)
    public String filter(WebRequest request, @RequestParam String startDate, @RequestParam String endDate) {
        LocalDate startDateAsDate = parseLocalDate(startDate);
        LocalDate endDateAsDate = parseLocalDate(endDate);
//        LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
//        LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
        LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
        LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
        List<Meal> meals = service.getBetweenDates(startDateAsDate, endDateAsDate, getAuthUserId());
        List<MealTo> finalMeals = MealsUtil.getFilteredTos(meals, SecurityUtil.authUserCaloriesPerDay(), startTime, endTime);
        request.setAttribute("meals", finalMeals, RequestAttributes.SCOPE_REQUEST);
        return PAGE_LIST;
    }

    private Meal createMealStub() {
        Meal meal = new Meal();
        meal.setDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        meal.setDescription("A new meal...");
        meal.setCalories(111);
        return meal;
    }

    private int getAuthUserId() {
        return SecurityUtil.authUserId();
    }

}
