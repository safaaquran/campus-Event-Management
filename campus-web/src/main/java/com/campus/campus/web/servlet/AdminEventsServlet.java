package com.campus.campus.web.servlet;

import com.campus.campus.web.dao.EventDAO;
import com.campus.campus.web.model.Event;
import com.campus.campus.web.model.EventStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/secure/admin/events")
public class AdminEventsServlet extends HttpServlet {
    private final EventDAO eventDAO = new EventDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("events", eventDAO.findAll());
            request.getRequestDispatcher("/secure/admin-events.jsp").forward(request, response);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        long eventId = Long.parseLong(request.getParameter("eventId"));
        try {
            if ("delete".equals(action)) {
                eventDAO.delete(eventId);
            } else if ("update".equals(action)) {
                Event event = eventDAO.findById(eventId);
                if (event != null) {
                    event.setTitle(request.getParameter("title"));
                    event.setLocation(request.getParameter("location"));
                    event.setEventDateTime(LocalDateTime.parse(request.getParameter("eventDateTime")));
                    int newCapacity = Integer.parseInt(request.getParameter("capacity"));
                    int reserved = event.getCapacity() - event.getSeatsRemaining();
                    if (newCapacity >= reserved) {
                        event.setCapacity(newCapacity);
                        event.setSeatsRemaining(newCapacity - reserved);
                    }
                    eventDAO.update(event);
                }
            } else if ("close".equals(action)) {
                eventDAO.setStatus(eventId, EventStatus.CLOSED);
            } else if ("open".equals(action)) {
                eventDAO.setStatus(eventId, EventStatus.OPEN);
            }
            response.sendRedirect(request.getContextPath() + "/secure/admin/events");
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
}
