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

@WebServlet("/secure/reservations")
public class ReservationServlet extends HttpServlet {
    private final ReservationDAO reservationDAO = new ReservationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = SessionUtil.currentUser(request);
        if (user == null || user.getRole() != Role.STUDENT) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Reservations are available only to students.");
            return;
        }
        try {
            request.setAttribute("reservations", reservationDAO.findByStudent(user.getId()));
            request.getRequestDispatcher("/secure/reservations.jsp").forward(request, response);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = SessionUtil.currentUser(request);
        if (user.getRole() != Role.STUDENT) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only students can reserve tickets.");
            return;
        }
        String action = request.getParameter("action");
        long eventId = Long.parseLong(request.getParameter("eventId"));
        String redirect = request.getParameter("redirect");
        boolean detailsRedirect = "details".equals(redirect);
        String target = detailsRedirect
                ? request.getContextPath() + "/secure/student/event-details?eventId=" + eventId + "&result="
                : request.getContextPath() + "/secure/student/dashboard?result=";
        try {
            boolean ok;
            if ("cancel".equals(action)) {
                ok = reservationDAO.cancelReservation(eventId, user.getId());
                if (detailsRedirect) {
                    response.sendRedirect(target + (ok ? "cancelled" : "failed"));
                } else {
                    response.sendRedirect(request.getContextPath() + "/secure/reservations?result=" + (ok ? "cancelled" : "failed"));
                }
                return;
            }
            ok = reservationDAO.reserveTicket(eventId, user.getId());
            response.sendRedirect(target + (ok ? "reserved" : "failed"));
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
}
