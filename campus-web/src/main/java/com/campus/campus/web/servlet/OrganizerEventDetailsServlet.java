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

@WebServlet("/secure/organizer/event-details")
public class OrganizerEventDetailsServlet extends HttpServlet {
    private final EventDAO eventDAO = new EventDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = SessionUtil.currentUser(request);
        if (user == null || user.getRole() != Role.ORGANIZER) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Organizer access required.");
            return;
        }

        String eventIdRaw = request.getParameter("eventId");
        if (eventIdRaw == null || eventIdRaw.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/secure/organizer/dashboard");
            return;
        }

        try {
            long eventId = Long.parseLong(eventIdRaw);
            Event event = eventDAO.findById(eventId);
            if (event == null || event.getOrganizerId() != user.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You can view only your own events.");
                return;
            }
            request.setAttribute("event", event);
            request.setAttribute("registeredCount", eventDAO.countAttendees(event.getId()));
            request.getRequestDispatcher("/secure/organizer-event-details.jsp").forward(request, response);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
}
