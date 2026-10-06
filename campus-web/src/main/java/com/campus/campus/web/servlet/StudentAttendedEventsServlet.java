package com.campus.campus.web.servlet;

import com.campus.campus.web.dao.ReservationDAO;
import com.campus.campus.web.model.Role;
import com.campus.campus.web.model.User;
import com.campus.campus.web.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/secure/student/attended-events")
public class StudentAttendedEventsServlet extends HttpServlet {
    private final ReservationDAO reservationDAO = new ReservationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = SessionUtil.currentUser(request);
        if (user == null || user.getRole() != Role.STUDENT) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Student access required.");
            return;
        }
        try {
            request.setAttribute("reservations", reservationDAO.findPastByStudent(user.getId()));
            request.getRequestDispatcher("/secure/student-attended-events.jsp").forward(request, response);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
}
