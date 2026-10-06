package com.campus.campus.web.servlet;

import com.campus.campus.web.dao.EventDAO;
import com.campus.campus.web.model.Event;
import com.campus.campus.web.model.Role;
import com.campus.campus.web.model.User;
import com.campus.campus.web.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/secure/organizer/dashboard")
public class OrganizerDashboardServlet extends HttpServlet {
    private final EventDAO eventDAO = new EventDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = SessionUtil.currentUser(request);
        if (user == null || user.getRole() != Role.ORGANIZER) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Organizer access required.");
            return;
        }
        try {
            eventDAO.markExpiredEvents();
            List<Event> events = eventDAO.findByOrganizer(user.getId());
            String statusFilter = nullableTrim(request.getParameter("status"));
            String dateFilter = nullableTrim(request.getParameter("date"));
            events.removeIf(event -> !matches(event, statusFilter, dateFilter));

            Map<Long, Integer> attendeeCounts = new HashMap<>();
            for (Event event : events) {
                attendeeCounts.put(event.getId(), eventDAO.countAttendees(event.getId()));
            }

            request.setAttribute("events", events);
            request.setAttribute("attendeeCounts", attendeeCounts);
            request.getRequestDispatcher("/secure/organizer-dashboard.jsp").forward(request, response);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    private String nullableTrim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean matches(Event event, String statusFilter, String dateFilter) {
        if (statusFilter != null && !statusFilter.equals("ALL")) {
            String smartStatus = smartStatus(event);
            if (!smartStatus.equals(statusFilter)) {
                return false;
            }
        }
        if (dateFilter != null) {
            LocalDate requested = LocalDate.parse(dateFilter);
            if (event.getEventDateTime() == null || !event.getEventDateTime().toLocalDate().equals(requested)) {
                return false;
            }
        }
        return true;
    }

    private String smartStatus(Event event) {
        if (event.getStatus() != null && event.getStatus().name().equals("EXPIRED")) {
            return "EXPIRED";
        }
        if (event.isCompleted()) {
            return "COMPLETED";
        }
        if (event.getSeatsRemaining() <= 0) {
            return "FULL";
        }
        return "ACTIVE";
    }
}
