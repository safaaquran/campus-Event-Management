package com.campus.campus.web.servlet;

import com.campus.campus.web.dao.EventDAO;
import com.campus.campus.web.model.Event;
import com.campus.campus.web.model.EventCategory;
import com.campus.campus.web.model.Role;
import com.campus.campus.web.model.EventStatus;
import com.campus.campus.web.model.User;
import com.campus.campus.web.util.SessionUtil;
import com.campus.campus.web.util.UploadStorageUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/secure/organizer/manage-events")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024L, maxRequestSize = 6 * 1024 * 1024L)
public class OrganizerManageEventsServlet extends HttpServlet {
    private final EventDAO eventDAO = new EventDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            User user = SessionUtil.currentUser(request);
            if (user == null || user.getRole() != Role.ORGANIZER) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Organizer access required.");
                return;
            }
            eventDAO.markExpiredEvents();
            List<Event> events = eventDAO.findByOrganizer(user.getId());
            String editEventId = request.getParameter("editEventId");
            if (editEventId != null && !editEventId.isBlank()) {
                request.setAttribute("editEventId", editEventId.trim());
            }
            Map<Long, Integer> attendeeCounts = new HashMap<>();
            for (Event event : events) {
                attendeeCounts.put(event.getId(), eventDAO.countAttendees(event.getId()));
            }
            request.setAttribute("events", events);
            request.setAttribute("attendeeCounts", attendeeCounts);
            request.getRequestDispatcher("/secure/organizer-manage-events.jsp").forward(request, response);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            User user = SessionUtil.currentUser(request);
            if (user == null || user.getRole() != Role.ORGANIZER) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Organizer access required.");
                return;
            }
            long eventId = Long.parseLong(request.getParameter("eventId"));
            Event event = eventDAO.findById(eventId);
            if (!canAccessEvent(user, event)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You can manage only your own events.");
                return;
            }
            switch (action) {
                case "delete" -> eventDAO.delete(eventId);
                case "close" -> eventDAO.setStatus(eventId, EventStatus.CLOSED);
                case "complete" -> eventDAO.markCompleted(eventId, true);
                case "update" -> updateEvent(request, event);
                default -> {
                }
            }
            String result = switch (action) {
                case "delete" -> "deleted";
                case "close" -> "closed";
                case "complete" -> "completed";
                case "update" -> "updated";
                default -> "updated";
            };
            response.sendRedirect(request.getContextPath() + "/secure/organizer/manage-events?result=" + result);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    private void updateEvent(HttpServletRequest request, Event existing) throws Exception {
        if (existing == null) {
            return;
        }
        existing.setTitle(request.getParameter("title"));
        existing.setOrganizerName(request.getParameter("organizerName"));
        existing.setDescription(request.getParameter("description"));
        existing.setDepartmentClub(request.getParameter("departmentClub"));
        existing.setEventDateTime(LocalDateTime.parse(request.getParameter("eventDateTime")));
        existing.setLocation(request.getParameter("location"));
        existing.setCategory(parseCategory(request.getParameter("category")));
        String newImage = storeImage(request);
        if (newImage != null) {
            existing.setEventImage(newImage);
        }

        int newCapacity = Integer.parseInt(request.getParameter("capacity"));
        int reservedCount = existing.getCapacity() - existing.getSeatsRemaining();
        if (newCapacity >= reservedCount) {
            existing.setCapacity(newCapacity);
            existing.setSeatsRemaining(newCapacity - reservedCount);
        }
        eventDAO.update(existing);
    }

    private boolean canAccessEvent(User user, Event event) {
        if (user == null || event == null) {
            return false;
        }
        return user.getRole() == Role.ORGANIZER && event.getOrganizerId() == user.getId();
    }

    private String storeImage(HttpServletRequest request) throws Exception {
        Part imagePart = request.getPart("eventImage");
        if (imagePart == null || imagePart.getSize() <= 0) {
            return null;
        }
        Path uploadsDir = resolveUploadDirectory(request);
        Files.createDirectories(uploadsDir);
        String originalName = imagePart.getSubmittedFileName();
        String fileName = UploadStorageUtil.buildStoredFileName(originalName);
        Path target = uploadsDir.resolve(fileName);
        try (InputStream inputStream = imagePart.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return fileName;
    }

    private EventCategory parseCategory(String categoryValue) {
        if (categoryValue == null) {
            throw new IllegalArgumentException("Category is required.");
        }
        String normalized = categoryValue.trim().toUpperCase().replace('-', '_').replace(' ', '_');
        return EventCategory.valueOf(normalized);
    }

    private Path resolveUploadDirectory(HttpServletRequest request) {
        return UploadStorageUtil.resolvePersistentUploadDirectory(request);
    }
}
