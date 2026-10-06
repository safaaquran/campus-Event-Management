package com.campus.campus.web.servlet;

import com.campus.campus.web.dao.EventDAO;
import com.campus.campus.web.dao.LookupDAO;
import com.campus.campus.web.model.Event;
import com.campus.campus.web.model.EventCategory;
import com.campus.campus.web.model.Role;
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

@WebServlet("/secure/organizer/edit-event")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024L, maxRequestSize = 6 * 1024 * 1024L)
public class OrganizerEditEventServlet extends HttpServlet {
    private final EventDAO eventDAO = new EventDAO();
    private final LookupDAO lookupDAO = new LookupDAO();

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
            response.sendRedirect(request.getContextPath() + "/secure/organizer/manage-events");
            return;
        }
        try {
            long eventId = Long.parseLong(eventIdRaw);
            Event event = eventDAO.findById(eventId);
            if (event == null || event.getOrganizerId() != user.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You can edit only your own events.");
                return;
            }
            request.setAttribute("departments", lookupDAO.departments());
            request.setAttribute("categories", lookupDAO.categories());
            request.setAttribute("event", event);
            request.getRequestDispatcher("/secure/organizer-edit-event.jsp").forward(request, response);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = SessionUtil.currentUser(request);
        if (user == null || user.getRole() != Role.ORGANIZER) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Organizer access required.");
            return;
        }
        try {
            long eventId = Long.parseLong(request.getParameter("eventId"));
            Event event = eventDAO.findById(eventId);
            if (event == null || event.getOrganizerId() != user.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "You can edit only your own events.");
                return;
            }

            event.setTitle(request.getParameter("title"));
            event.setOrganizerName(request.getParameter("organizerName"));
            event.setDescription(request.getParameter("description"));
            event.setDepartmentClub(request.getParameter("departmentClub"));
            event.setEventDateTime(LocalDateTime.parse(request.getParameter("eventDateTime")));
            event.setLocation(request.getParameter("location"));
            event.setCategory(parseCategory(request.getParameter("category")));
            String newImage = storeImage(request);
            if (newImage != null) {
                event.setEventImage(newImage);
            }

            int newCapacity = Integer.parseInt(request.getParameter("capacity"));
            int reservedCount = event.getCapacity() - event.getSeatsRemaining();
            if (newCapacity >= reservedCount) {
                event.setCapacity(newCapacity);
                event.setSeatsRemaining(newCapacity - reservedCount);
            }

            eventDAO.update(event);
            response.sendRedirect(request.getContextPath() + "/secure/organizer/manage-events?result=updated");
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
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
