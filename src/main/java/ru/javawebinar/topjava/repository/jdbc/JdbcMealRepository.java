package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JdbcMealRepository implements MealRepository {

    private static final String TABLE_NAME = "meals";
    private static final BeanPropertyRowMapper<Meal> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Meal.class);
    private static final String ORDER_BY = " ORDER BY Date_Trunc('day',datetime) DESC, Cast(datetime AS time)";

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate parametersJdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public JdbcMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameters) {
        this.jdbcTemplate = jdbcTemplate;
        this.parametersJdbcTemplate = namedParameters;

        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                            .withTableName(TABLE_NAME)
                            .usingGeneratedKeyColumns("id")
        ;
    }

    @Override
    public Meal save(Meal meal, int userId) {
        MapSqlParameterSource data = new MapSqlParameterSource();
        data.addValue("id", meal.getId())
            .addValue("datetime", meal.getDateTime())
            .addValue("description", meal.getDescription())
            .addValue("calories", meal.getCalories())
            .addValue("user_id", userId)
        ;
        if (meal.isNew()) {
            Number newId = jdbcInsert.executeAndReturnKey(data);
            if (newId != null)
                meal.setId(newId.intValue());
            //end if
        } else {
            int rowsAffected = parametersJdbcTemplate.update(
                                    "UPDATE " + TABLE_NAME + " SET "
                                        + " description = :description, "
                                        + " datetime = :datetime, "
                                        + " calories = :calories "
                                        + " WHERE id = :id "
                                        + "  AND user_id = :user_id",
                                    data);
            if (rowsAffected == 0)
                return null;
            //end if
        }

        return meal;
    }

    @Override
    public boolean delete(int id, int userId) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM " + TABLE_NAME + " WHERE id=? AND user_id=?", id, userId);
        return rowsAffected == 1;
    }

    @Override
    public Meal get(int id, int userId) {
        List<Meal> meals = jdbcTemplate.query("SELECT * FROM " + TABLE_NAME + " WHERE id=? AND user_id=?", ROW_MAPPER, id, userId);

        if (meals == null || meals.isEmpty())
            return null;
        //end if
        if (meals.size() > 1)
            throw new IncorrectResultSizeDataAccessException("Incorrect size of result-set of Meals", 1, meals.size());
        //end if

        return meals.get(0);
    }

    @Override
    public List<Meal> getAll(int userId) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE user_id=?" + ORDER_BY;
        return jdbcTemplate.query(sql, ROW_MAPPER, userId);
    }

    @Override
    public List<Meal> getBetween(LocalDateTime startDate, LocalDateTime endDate, int userId) {
        String sql = "SELECT * FROM " + TABLE_NAME
                   + " WHERE user_id=:user_id AND datetime>=:start_date AND datetime<=:end_date "
                   + ORDER_BY;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId)
            .addValue("start_date", startDate)
            .addValue("end_date", endDate);
        return parametersJdbcTemplate.query(sql, params, ROW_MAPPER);
///        return jdbcTemplate.query(sql, ROW_MAPPER, startDate, endDate, userId);
    }

}
