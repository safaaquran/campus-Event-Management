package com.campus.campus.web.servlet;

import com.campus.campus.web.dao.EventDAO;
import com.campus.campus.web.factory.EventFactory;
import com.campus.campus.web.factory.EventFactoryProvider;
import com.campus.campus.web.model.Event;
import com.campus.campus.web.model.EventCategory;
import com.campus.campus.web.model.EventCreationRequest;
import com.campus.campus.web.model.Role;
import com.campus.campus.web.model.EventType;
import com.campus.campus.web.model.User;
import com.campus.campus.web.dao.LookupDAO;
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

@WebServlet("/secure/organizer/create-event")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024L, maxRequestSize = 6 * 1024 * 1024L)
public class OrganizerCreateEventServlet extends HttpServlet {
    private final EventDAO eventDAO = new EventDAO();
    private final EventFactoryProvider factoryProvider = new EventFactoryProvider();
    private final LookupDAO lookupDAO = new LookupDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = SessionUtil.currentUser(request);
        if (user == null || user.getRole() != Role.ORGANIZER) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Organizer access required.");
            return;
        }
        try {
            request.setAttribute("departments", lookupDAO.departments());
            request.setAttribute("categories", lookupDAO.categories());
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
        request.getRequestDispatcher("/secure/organizer-create-event.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            User organizer = SessionUtil.currentUser(request);
            if (organizer == null || organizer.getRole() != Role.ORGANIZER) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Organizer access required.");
                return;
            }
            EventType type = EventType.valueOf(request.getParameter("eventType"));
            EventFactory eventFactory = factoryProvider.factoryFor(type);
            EventCreationRequest creationRequest = buildRequest(request);
            Event event = eventFactory.createEvent(organizer, creationRequest);
            eventDAO.create(event);
            response.sendRedirect(request.getContextPath() + "/secure/organizer/manage-events?result=created");
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    private EventCreationRequest buildRequest(HttpServletRequest request) throws Exception {
        EventCreationRequest creationRequest = new EventCreationRequest();
        creationRequest.setTitle(request.getParameter("title"));
        creationRequest.setOrganizerName(request.getParameter("organizerName"));
        creationRequest.setDescription(request.getParameter("description"));
        creationRequest.setDepartmentClub(request.getParameter("departmentClub"));
        creationRequest.setEventDateTime(LocalDateTime.parse(request.getParameter("eventDateTime")));
        creationRequest.setLocation(request.getParameter("location"));
        creationRequest.setCapacity(Integer.parseInt(request.getParameter("capacity")));
        creationRequest.setCategory(parseCategory(request.getParameter("category")));
        creationRequest.setEventImage(storeImage(request));
        return creationRequest;
    }

    private EventCategory parseCategory(String categoryValue) {
        if (categoryValue == null) {
            throw new IllegalArgumentException("Category is required.");
        }
        String normalized = categoryValue.trim().toUpperCase().replace('-', '_').replace(' ', '_');
        return EventCategory.valueOf(normalized);
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

    private Path resolveUploadDirectory(HttpServletRequest request) {
        return UploadStorageUtil.resolvePersistentUploadDirectory(request);
    }
}
