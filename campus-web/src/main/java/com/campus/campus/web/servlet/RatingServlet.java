package com.campus.campus.web.servlet;

import com.campus.campus.web.dao.RatingDAO;
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

@WebServlet("/secure/ratings")
public class RatingServlet extends HttpServlet {
    private final RatingDAO ratingDAO = new RatingDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = SessionUtil.currentUser(request);
        if (user == null || user.getRole() != Role.STUDENT) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        long eventId = Long.parseLong(request.getParameter("eventId"));
        int rating = Integer.parseInt(request.getParameter("rating"));
        String feedback = request.getParameter("feedback");
        String redirect = request.getParameter("redirect");
        String target;
        if ("details".equals(redirect)) {
            target = request.getContextPath() + "/secure/student/event-details?eventId=" + eventId + "&result=";
        } else if ("attended".equals(redirect)) {
            target = request.getContextPath() + "/secure/student/attended-events?result=";
        } else {
            target = request.getContextPath() + "/secure/student/dashboard?result=";
        }
        if (rating < 1 || rating > 5) {
            response.sendRedirect(target + "ratingFailed");
            return;
        }
        try {
            if (!reservationDAO.canRate(eventId, user.getId())) {
                response.sendRedirect(target + "ratingFailed");
                return;
            }
            ratingDAO.upsertRating(eventId, user.getId(), rating, feedback);
            response.sendRedirect(target + "rated");
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
}
