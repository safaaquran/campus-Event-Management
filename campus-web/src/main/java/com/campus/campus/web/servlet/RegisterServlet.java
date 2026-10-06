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

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String faculty = request.getParameter("faculty");
        String department = request.getParameter("department");
        String admissionYearRaw = request.getParameter("admissionYear");
        String roleRaw = request.getParameter("role");

        if (isBlank(fullName, email, password, faculty, department, admissionYearRaw, roleRaw)) {
            response.sendRedirect(request.getContextPath() + "/signup.jsp?error=missing");
            return;
        }
        try {
            String normalizedEmail = email.trim().toLowerCase();
            if (userDAO.findByEmail(normalizedEmail) != null) {
                response.sendRedirect(request.getContextPath() + "/signup.jsp?error=emailExists");
                return;
            }
            User user = new User();
            user.setFullName(fullName.trim());
            user.setEmail(normalizedEmail);
            user.setPasswordHash(PasswordUtil.sha256(password));
            user.setFaculty(faculty.trim());
            user.setDepartment(department.trim());
            user.setAdmissionYear(Integer.parseInt(admissionYearRaw));
            user.setRole(Role.valueOf(roleRaw));
            user.setBlocked(false);
            userDAO.create(user);
            response.sendRedirect(request.getContextPath() + "/login.jsp?success=registered");
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    private boolean isBlank(String... values) {
        for (String value : values) {
            if (value == null || value.isBlank()) {
                return true;
            }
        }
        return false;
    }
}
