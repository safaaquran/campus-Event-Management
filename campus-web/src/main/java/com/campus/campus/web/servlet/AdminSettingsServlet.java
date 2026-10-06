package com.campus.campus.web.servlet;

import com.campus.campus.web.dao.LookupDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/secure/admin/settings")
public class AdminSettingsServlet extends HttpServlet {
    private final LookupDAO lookupDAO = new LookupDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("departments", lookupDAO.departments());
            request.setAttribute("categories", lookupDAO.categories());
            request.getRequestDispatcher("/secure/admin-settings.jsp").forward(request, response);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type = request.getParameter("type");
        String value = request.getParameter("value");
        try {
            if ("department".equals(type)) {
                lookupDAO.addDepartment(value);
            } else if ("category".equals(type)) {
                lookupDAO.addCategory(value);
            }
            response.sendRedirect(request.getContextPath() + "/secure/admin/settings");
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
}
