package com.campus.campus.web.servlet;

import com.campus.campus.web.dao.EventDAO;
import com.campus.campus.web.dao.RatingDAO;
import com.campus.campus.web.model.Event;
import com.campus.campus.web.model.Role;
import com.campus.campus.web.model.User;
import com.campus.campus.web.strategy.search.EventSearchService;
import com.campus.campus.web.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/secure/student/dashboard")
public class StudentDashboardServlet extends HttpServlet {
    private final EventDAO eventDAO = new EventDAO();
    private final RatingDAO ratingDAO = new RatingDAO();
    private final EventSearchService searchService = new EventSearchService(eventDAO);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = SessionUtil.currentUser(request);
        if (user == null || user.getRole() != Role.STUDENT) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Student access required.");
            return;
        }
        try {
            eventDAO.markExpiredEvents();
            String q = nullableTrim(request.getParameter("q"));
            String category = nullableTrim(request.getParameter("category"));
            String department = nullableTrim(request.getParameter("department"));
            String type = nullableTrim(request.getParameter("type"));
            String date = nullableTrim(request.getParameter("date"));
            String time = nullableTrim(request.getParameter("time"));

            // Start from availability strategy so list is always open + has seats.
            List<Event> events = searchService.strategyFor("AVAILABILITY").search("");
            events = applyStrategy(events, "TITLE", q);
            events = applyStrategy(events, "CATEGORY", category);
            events = applyStrategy(events, "DEPARTMENT", department);
            events = applyStrategy(events, "TYPE", type);
            events = applyStrategy(events, "DATE", date);
            events.removeIf(event -> !matchesTime(event, time));
            for (Event event : events) {
                event.setAverageRating(ratingDAO.averageForEvent(event.getId()));
            }
            request.setAttribute("departments", eventDAO.findDistinctDepartmentsForOpenEvents());
            request.setAttribute("events", events);
            request.getRequestDispatcher("/secure/student-dashboard.jsp").forward(request, response);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    private String nullableTrim(String value) {
        if (value == null) {
            return null;
        }
        String v = value.trim();
        return v.isEmpty() ? null : v;
    }

    private List<Event> applyStrategy(List<Event> base, String filter, String query) throws Exception {
        if (query == null) {
            return base;
        }
        List<Event> strategyEvents = searchService.strategyFor(filter).search(query);
        Map<Long, Event> strategyById = new LinkedHashMap<>();
        for (Event event : strategyEvents) {
            strategyById.put(event.getId(), event);
        }
        List<Event> matched = new ArrayList<>();
        for (Event event : base) {
            Event strategyMatch = strategyById.get(event.getId());
            if (strategyMatch != null) {
                matched.add(event);
            }
        }
        return matched;
    }

    private boolean matchesTime(Event event, String time) {
        if (time == null) {
            return true;
        }
        LocalTime requestedTime = LocalTime.parse(time);
        if (event.getEventDateTime() == null || !event.getEventDateTime().toLocalTime().withSecond(0).withNano(0)
                .equals(requestedTime.withSecond(0).withNano(0))) {
            return false;
        }
        return true;
    }
}
