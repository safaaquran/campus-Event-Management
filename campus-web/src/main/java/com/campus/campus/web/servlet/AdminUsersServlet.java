package com.campus.campus.web.servlet;

import com.campus.campus.web.dao.UserDAO;
import com.campus.campus.web.model.Role;
import com.campus.campus.web.model.User;
import com.campus.campus.web.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/secure/admin/users")
public class AdminUsersServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("users", userDAO.findAll());
            request.getRequestDispatcher("/secure/admin-users.jsp").forward(request, response);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            if ("create".equals(action)) {
                createUser(request);
            } else if ("update".equals(action)) {
                long userId = Long.parseLong(request.getParameter("userId"));
                User user = userDAO.findById(userId);
                if (user != null) {
                    user.setFullName(request.getParameter("fullName"));
                    user.setEmail(request.getParameter("email").trim().toLowerCase());
                    user.setRole(Role.valueOf(request.getParameter("role")));
                    user.setFaculty(request.getParameter("faculty"));
                    user.setDepartment(request.getParameter("department"));
                    user.setAdmissionYear(Integer.parseInt(request.getParameter("admissionYear")));
                    userDAO.updateByAdmin(user);
                }
            } else if ("delete".equals(action)) {
                long userId = Long.parseLong(request.getParameter("userId"));
                userDAO.deleteUser(userId);
            } else if ("block".equals(action)) {
                long userId = Long.parseLong(request.getParameter("userId"));
                userDAO.blockOrUnblock(userId, true);
            } else if ("unblock".equals(action)) {
                long userId = Long.parseLong(request.getParameter("userId"));
                userDAO.blockOrUnblock(userId, false);
            }
            response.sendRedirect(request.getContextPath() + "/secure/admin/users");
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    private void createUser(HttpServletRequest request) throws Exception {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        String faculty = request.getParameter("faculty");
        String department = request.getParameter("department");
        String admissionYear = request.getParameter("admissionYear");
        if (fullName == null || email == null || password == null || role == null
                || faculty == null || department == null || admissionYear == null
                || fullName.isBlank() || email.isBlank() || password.isBlank() || role.isBlank()) {
            return;
        }
        String normalizedEmail = email.trim().toLowerCase();
        if (userDAO.findByEmail(normalizedEmail) != null) {
            return;
        }
        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(PasswordUtil.sha256(password));
        user.setRole(Role.valueOf(role));
        user.setFaculty(faculty.trim());
        user.setDepartment(department.trim());
        user.setAdmissionYear(Integer.parseInt(admissionYear));
        user.setBlocked(false);
        userDAO.create(user);
    }
}
