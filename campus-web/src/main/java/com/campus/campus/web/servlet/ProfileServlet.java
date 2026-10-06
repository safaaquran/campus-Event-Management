package com.campus.campus.web.servlet;

import com.campus.campus.web.dao.UserDAO;
import com.campus.campus.web.model.User;
import com.campus.campus.web.util.PasswordUtil;
import com.campus.campus.web.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/secure/profile")
public class ProfileServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/secure/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = SessionUtil.currentUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String faculty = request.getParameter("faculty");
        String department = request.getParameter("department");
        String admissionYear = request.getParameter("admissionYear");
        String newPassword = request.getParameter("newPassword");
        if (fullName == null || email == null || faculty == null || department == null || admissionYear == null
                || fullName.isBlank() || email.isBlank() || faculty.isBlank() || department.isBlank() || admissionYear.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/secure/profile?error=missing");
            return;
        }
        try {
            String normalizedEmail = email.trim().toLowerCase();
            if (userDAO.emailExistsForAnotherUser(normalizedEmail, user.getId())) {
                response.sendRedirect(request.getContextPath() + "/secure/profile?error=emailExists");
                return;
            }
            user.setFullName(fullName.trim());
            user.setEmail(normalizedEmail);
            user.setFaculty(faculty.trim());
            user.setDepartment(department.trim());
            user.setAdmissionYear(Integer.parseInt(admissionYear));
            if (newPassword != null && !newPassword.isBlank()) {
                user.setPasswordHash(PasswordUtil.sha256(newPassword));
            }
            userDAO.updateProfile(user);
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("user", user);
            }
            response.sendRedirect(request.getContextPath() + "/secure/profile?success=1");
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
}
