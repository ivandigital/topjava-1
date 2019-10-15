package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);

    private MealRestController controller;
    private ConfigurableApplicationContext appCtx;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log.info("initing...");
        appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml");
        controller = appCtx.getBean(MealRestController.class);
    }

    @Override
    public void destroy() {
        super.destroy();
        log.info("destroying...");
        appCtx.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");

        Integer mealId = (id.isEmpty() || id.equals("0")) ? null : Integer.valueOf(id);
        MealTo meal = new MealTo(mealId,
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")),
                false
        );

        log.info(mealId==null ? "Creating {}" : "Updating {}", meal);
        if (meal.isNew())
            controller.create(meal);
        else
            controller.update(mealId, meal);
        //end if
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                log.info("Delete {}", id);
                controller.delete(id);
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final MealTo meal;
                if ("create".equals(action))
                    meal = new MealTo(null, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "a meal", 1000, false);
                else
                    meal = controller.get(getId(request));
                //end if
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "all":
            default:
                log.info("getAll");
                List<MealTo> meals = null;
                if (request.getParameterMap().isEmpty()) {
                    // no filter conditions
                    log.info("no filter conditions");
                    meals = controller.getAll();
                }
                else {
                    LocalDate dateStart = null;
                    if (!StringUtils.isEmpty(request.getParameter("dateBegin")))
                        dateStart = LocalDate.parse(request.getParameter("dateBegin"));
                    //end if
                    LocalDate dateEnd = null;
                    if (!StringUtils.isEmpty(request.getParameter("dateEnd")))
                        dateEnd = LocalDate.parse(request.getParameter("dateEnd"));
                    //end if
                    log.info("filter conditions: dateStart={}, dateEnd={}", dateStart, dateEnd);
                    meals = controller.getByPeriod(dateStart, dateEnd);
                }
                request.setAttribute("meals", meals);
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
