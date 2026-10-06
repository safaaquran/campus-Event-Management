package com.campus.campus.web.servlet;

import com.campus.campus.web.model.Role;
import com.campus.campus.web.model.User;
import com.campus.campus.web.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/secure/dashboard")
public class DashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = SessionUtil.currentUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        if (user.getRole() == Role.STUDENT) {
            response.sendRedirect(request.getContextPath() + "/secure/student/dashboard");
        } else if (user.getRole() == Role.ORGANIZER) {
            response.sendRedirect(request.getContextPath() + "/secure/organizer/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/secure/admin/users");
        }
    }
}
