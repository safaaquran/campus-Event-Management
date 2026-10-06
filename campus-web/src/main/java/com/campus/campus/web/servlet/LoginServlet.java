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
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=invalid");
            return;
        }

        try {
            User user = userDAO.findByEmail(email.trim().toLowerCase());
            if (user == null || !user.getPasswordHash().equals(PasswordUtil.sha256(password))) {
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=invalid");
                return;
            }
            if (user.isBlocked()) {
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=blocked");
                return;
            }
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            if (user.getRole() == Role.STUDENT) {
                response.sendRedirect(request.getContextPath() + "/secure/student/dashboard");
            } else if (user.getRole() == Role.ORGANIZER) {
                response.sendRedirect(request.getContextPath() + "/secure/organizer/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/secure/admin/users");
            }
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
}
