package com.campus.campus.web.servlet;

import com.campus.campus.web.dao.EventDAO;
import com.campus.campus.web.dao.ReservationDAO;
import com.campus.campus.web.dao.RatingDAO;
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

@WebServlet("/secure/student/event-details")
public class StudentEventDetailsServlet extends HttpServlet {
    private final EventDAO eventDAO = new EventDAO();
    private final RatingDAO ratingDAO = new RatingDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = SessionUtil.currentUser(request);
        if (user == null || user.getRole() != Role.STUDENT) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Student access required.");
            return;
        }
        String eventIdRaw = request.getParameter("eventId");
        if (eventIdRaw == null || eventIdRaw.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/secure/student/dashboard");
            return;
        }
        try {
            long eventId = Long.parseLong(eventIdRaw);
            eventDAO.markExpiredEvents();
            Event event = eventDAO.findById(eventId);
            if (event == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Event not found.");
                return;
            }
            event.setAverageRating(ratingDAO.averageForEvent(event.getId()));
            request.setAttribute("hasReservation", reservationDAO.hasReservation(event.getId(), user.getId()));
            request.setAttribute("event", event);
            request.getRequestDispatcher("/secure/student-event-details.jsp").forward(request, response);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
}
