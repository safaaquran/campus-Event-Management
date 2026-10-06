package com.campus.campus.web.servlet;

import com.campus.campus.web.dao.EventDAO;
import com.campus.campus.web.dao.ReservationDAO;
import com.campus.campus.web.model.AttendanceStatus;
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

@WebServlet("/secure/organizer/attendance")
public class OrganizerAttendanceServlet extends HttpServlet {
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final EventDAO eventDAO = new EventDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String eventId = request.getParameter("eventId");
        if (eventId == null) {
            response.sendRedirect(request.getContextPath() + "/secure/organizer/manage-events");
            return;
        }
        try {
            long eventIdValue = Long.parseLong(eventId);
            User user = SessionUtil.currentUser(request);
            if (user == null || user.getRole() != Role.ORGANIZER) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Organizer access required.");
                return;
            }
            Event event = eventDAO.findById(eventIdValue);
            if (!canAccessEvent(user, event)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You can access attendance only for your own events.");
                return;
            }
            request.setAttribute("eventId", eventIdValue);
            request.setAttribute("reservations", reservationDAO.findByEvent(eventIdValue));
            request.getRequestDispatcher("/secure/organizer-attendance.jsp").forward(request, response);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long reservationId = Long.parseLong(request.getParameter("reservationId"));
        long eventId = Long.parseLong(request.getParameter("eventId"));
        AttendanceStatus status = AttendanceStatus.valueOf(request.getParameter("attendanceStatus"));
        try {
            User user = SessionUtil.currentUser(request);
            if (user == null || user.getRole() != Role.ORGANIZER) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Organizer access required.");
                return;
            }
            Event event = eventDAO.findById(eventId);
            if (!canAccessEvent(user, event)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You can access attendance only for your own events.");
                return;
            }
            reservationDAO.markAttendance(reservationId, status);
            response.sendRedirect(request.getContextPath() + "/secure/organizer/attendance?eventId=" + eventId);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    private boolean canAccessEvent(User user, Event event) {
        if (user == null || event == null) {
            return false;
        }
        return user.getRole() == Role.ORGANIZER && event.getOrganizerId() == user.getId();
    }
}
