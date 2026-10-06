package com.campus.campus.web.filter;

import com.campus.campus.web.model.Role;
import com.campus.campus.web.model.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/secure/organizer/*")
public class OrganizerFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null || (user.getRole() != Role.ORGANIZER && user.getRole() != Role.ADMIN)) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Organizer access required.");
            return;
        }
        chain.doFilter(request, response);
    }
}
