package com.campus.campus.web.util;

import com.campus.campus.web.model.Role;
import com.campus.campus.web.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class SessionUtil {
    private SessionUtil() {
    }

    public static User currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute("user");
        return value instanceof User ? (User) value : null;
    }

    public static boolean hasRole(HttpServletRequest request, Role role) {
        User user = currentUser(request);
        return user != null && user.getRole() == role;
    }
}
