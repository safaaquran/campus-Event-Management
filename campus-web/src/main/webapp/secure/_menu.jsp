<%@ page import="com.campus.campus.web.model.User,com.campus.campus.web.model.Role" %>
<%
    User menuUser = (User) session.getAttribute("user");
%>
<div class="nav">
    <a href="${pageContext.request.contextPath}/secure/dashboard">Dashboard</a>
    <% if (menuUser != null && menuUser.getRole() == Role.STUDENT) { %>
    <a href="${pageContext.request.contextPath}/secure/student/dashboard">Browse Events</a>
    <% } %>
    <a href="${pageContext.request.contextPath}/secure/profile">My Profile</a>
    <% if (menuUser != null && menuUser.getRole() == Role.ORGANIZER) { %>
    <a href="${pageContext.request.contextPath}/secure/organizer/dashboard">Organizer Dashboard</a>
    <% } %>
    <% if (menuUser != null && menuUser.getRole() == Role.ADMIN) { %>
    <a href="${pageContext.request.contextPath}/secure/admin/users">User Dashboard</a>
    <a href="${pageContext.request.contextPath}/secure/admin/events">Admin Events</a>
    <a href="${pageContext.request.contextPath}/secure/admin/settings">Admin Settings</a>
    <% } %>
    <a href="${pageContext.request.contextPath}/logout">Logout</a>
</div>
