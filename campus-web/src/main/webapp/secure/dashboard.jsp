<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.campus.campus.web.model.User" %>
<%
    User user = (User) request.getAttribute("user");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body>
<div class="container">
    <jsp:include page="_menu.jsp"/>
    <h2>Welcome, <%= user.getFullName() %></h2>
    <p>Role: <strong><%= user.getRole() %></strong></p>
    <p>Use the menu to browse events, reserve tickets, manage your profile, or open organizer/admin panels.</p>
</div>
</body>
</html>
