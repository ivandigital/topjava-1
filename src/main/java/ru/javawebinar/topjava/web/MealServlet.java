package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.service.MealServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class MealServlet extends HttpServlet {

    private static final String PAGE_LIST = "meals.jsp";
    private static final String PAGE_VIEW = "meal.jsp";

    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);

    private final MealService service = new MealServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.trace("Meals servlet is starting...");

        log.debug("Meals servlet -- Query: " + request.getQueryString());

        String pageNext = "";
        if (request.getParameterMap().isEmpty()) { // no parameters
            log.debug("No parameters --> Redirect to " + PAGE_LIST);
            pageNext = PAGE_LIST;
        }
        else if (! request.getParameter("id").isEmpty()) { // view a meal
            log.debug("Got parameter 'Id' --> Redirect to " + PAGE_VIEW);
            String id = request.getParameter("id");
            request.setAttribute("id", id);
            if (id != null && !id.isEmpty() && Integer.parseInt(id) != 0) {
                MealTo meal = service.get(Integer.parseInt(id));
                if (meal != null) {
                    request.setAttribute("meal", meal);
                }
                else {
                    log.warn("Cannot find meal by id=" + id);
                }
            }
            pageNext = PAGE_VIEW;
        }
        else { // default action
            log.debug("Unknown parameter --> Redirect to " + PAGE_LIST);
            pageNext = PAGE_LIST;
        }

        request.getRequestDispatcher(pageNext).forward(request, response);

        log.trace("Meals servlet has finished.");
    }

}
