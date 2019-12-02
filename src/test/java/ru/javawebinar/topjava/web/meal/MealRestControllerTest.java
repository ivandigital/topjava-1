package ru.javawebinar.topjava.web.meal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.javawebinar.topjava.MealTestData.assertMatch;
import static ru.javawebinar.topjava.TestUtil.readFromJson;

public class MealRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = MealRestController.REST_URL;

    @Autowired
    private MealService mealService;

    @Test
    void getAll() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL));
        result.andDo(print())
                .andExpect(handler().handlerType(MealRestController.class))
                .andExpect(handler().methodName("getAll"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().encoding("UTF-8"))
                .andExpect(jsonPath("$").isNotEmpty())
        ;
    }

    @Test
    void get() throws Exception {
        String url = REST_URL + "/" + MealTestData.MEAL1_ID;
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url));
        result.andDo(print())
                .andExpect(handler().handlerType(MealRestController.class))
                .andExpect(handler().methodName("getMeal"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().encoding("UTF-8"))
                .andExpect(jsonPath("$").isNotEmpty())
        ;
    }

    @Test
    void getInvalidMeal() throws Exception {
        int invalidMealId = 13;
        String url = REST_URL + "/" + invalidMealId;
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(url));
        result.andDo(print())
                .andExpect(handler().handlerType(MealRestController.class))
                .andExpect(handler().methodName("getMeal"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""))
        ;
    }

    @Test
    void create() throws Exception {
        Meal newMeal = MealTestData.getNew();
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(JsonUtil.writeValue(newMeal))
        );
        result.andDo(print())
                .andExpect(handler().handlerType(MealRestController.class))
                .andExpect(handler().methodName("createMeal"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(redirectedUrlPattern(ServletUriComponentsBuilder.fromCurrentContextPath().path(REST_URL + "/*").toUriString()))
        ;

        Meal created = readFromJson(result, Meal.class);
        newMeal.setId(created.getId());
        assertMatch(newMeal, created);
    }

    @Test
    void update() throws Exception {
        Meal changedMeal = MealTestData.getUpdated();
        int changedMealId = changedMeal.getId();
        String url = REST_URL + "/" + changedMealId;
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(JsonUtil.writeValue(changedMeal))
        );
        result.andDo(print())
                .andExpect(handler().handlerType(MealRestController.class))
                .andExpect(handler().methodName("update"))
                .andExpect(status().isOk())
        ;
        Meal rereadMeal = mealService.get(changedMealId, UserTestData.USER_ID);
        assertMatch(changedMeal, rereadMeal);
    }

    @Test
    void delete() throws Exception {
        int mealIdToDelete = MealTestData.MEAL1_ID;
        String url = REST_URL + "/" + mealIdToDelete;
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete(url));
        result.andDo(print())
                .andExpect(handler().handlerType(MealRestController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isNoContent())
        ;

        assertThrows(NotFoundException.class, () -> mealService.get(mealIdToDelete, UserTestData.USER_ID));
    }

    @Test
    void getBetweenDates() throws Exception {
        LocalDate startDate = LocalDate.of(2015, Month.MAY, 30);
        LocalDate endDate = LocalDate.of(2015, Month.MAY, 30);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(REST_URL + "/filter?startDate={startDate}&endDate={endDate}", startDate, endDate);
        ResultActions result = mockMvc.perform(requestBuilder);
        checkGeneralJsonResponse(result)
                .andExpect(handler().handlerType(MealRestController.class))
                .andExpect(handler().methodName("getBetween"))
                .andExpect(jsonPath("$").isNotEmpty())
        ;
    }

    @Test
    void getBetweenTimes() throws Exception {
        LocalTime startTime = LocalTime.of(11, 0);
        LocalTime endTime = LocalTime.of(20, 0);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(getFilterUrl())
                .param("startTime", startTime.toString())
                .param("endTime", endTime.toString())
        ;
        ResultActions result = mockMvc.perform(requestBuilder);
        checkGeneralJsonResponse(result)
                .andExpect(handler().handlerType(MealRestController.class))
                .andExpect(handler().methodName("getBetween"))
                .andExpect(jsonPath("$").isNotEmpty())
        ;
    }

    @Test
    void getBetweenWithoutEndDate() throws Exception {
        LocalDate startDate = LocalDate.of(2015, Month.MAY, 30);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(getFilterUrl())
                .param("startDate", startDate.toString())
                ;
        ResultActions result = mockMvc.perform(requestBuilder);
        checkGeneralJsonResponse(result)
                .andExpect(handler().handlerType(MealRestController.class))
                .andExpect(handler().methodName("getBetween"))
                .andExpect(jsonPath("$").isNotEmpty())
        ;
    }

    @Test
    void getBetweenWithoutStartDate() throws Exception {
        LocalDate endDate = LocalDate.of(2015, Month.MAY, 30);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(getFilterUrl())
                .param("endDate", endDate.toString())
                ;
        ResultActions result = mockMvc.perform(requestBuilder);
        checkGeneralJsonResponse(result)
                .andExpect(handler().handlerType(MealRestController.class))
                .andExpect(handler().methodName("getBetween"))
                .andExpect(jsonPath("$").isNotEmpty())
        ;
    }

    private ResultActions checkGeneralJsonResponse(ResultActions result) throws Exception {
        return result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().encoding("UTF-8"))
        ;
    }

    private String getFilterUrl() {
        return REST_URL + "/filter";
    }

}
