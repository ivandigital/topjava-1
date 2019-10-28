package ru.javawebinar.topjava.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@NamedQuery(name = Meal.READ, query = "SELECT m FROM Meal m WHERE m.id=:id AND m.user.id=:user_id")
@NamedQuery(name = Meal.READ_ALL, query = "SELECT m FROM Meal m WHERE m.user.id=:user_id ORDER BY m.dateTime DESC")
@NamedQuery(name = Meal.READ_BY_INTERVAL, query = "SELECT m FROM Meal m WHERE m.user.id=:user_id AND m.dateTime>=:startDate AND m.dateTime<=:endDate ORDER BY m.dateTime DESC")
@NamedQuery(name = Meal.DELETE, query = "DELETE FROM Meal m WHERE m.id=:id AND m.user.id=:user_id")

@Entity
@Table(name = "meals", uniqueConstraints = {@UniqueConstraint(name = "meals_unique_idx", columnNames = {"user_id", "date_time"})})
public class Meal extends AbstractBaseEntity {

    public static final String READ             = "Meal.get";
    public static final String READ_ALL         = "Meal.getAll";
    public static final String READ_BY_INTERVAL = "Meal.getByInterval";
    public static final String DELETE           = "Meal.delete";

    @Column(name = "date_time", nullable = false)
    @NotNull
    private LocalDateTime dateTime;

    @Column(name = "description", nullable = false)
    @NotBlank
    @NotNull
    @Size(min = 1, max = 100)
    private String description;

    @Column(name = "calories", nullable = false)
    @NotNull
    @PositiveOrZero
    private int calories;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private User user;

    public Meal() {
    }

    public Meal(LocalDateTime dateTime, String description, int calories) {
        this(null, dateTime, description, calories);
    }

    public Meal(Integer id, LocalDateTime dateTime, String description, int calories) {
        super(id);
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    public LocalDate getDate() {
        return dateTime.toLocalDate();
    }

    public LocalTime getTime() {
        return dateTime.toLocalTime();
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return String.format(
                "Meal {id=%d, dateTime=%s, description='%s', calories=%d, user_id=%s}",
                id, dateTime, description, calories, user!=null ? user.getId() : "<NULL>"
        );
    }

}
