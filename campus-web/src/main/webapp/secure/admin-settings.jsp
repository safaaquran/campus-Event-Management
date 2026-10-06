<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%
    List<String> departments = (List<String>) request.getAttribute("departments");
    List<String> categories = (List<String>) request.getAttribute("categories");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Settings</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body>
<div class="container">
    <jsp:include page="_menu.jsp"/>
    <h2>Admin - System Settings</h2>
    <div class="row">
        <div class="col">
            <h3>Departments</h3>
            <ul>
                <% for (String department : departments) { %>
                <li><%= department %></li>
                <% } %>
            </ul>
            <form method="post" action="${pageContext.request.contextPath}/secure/admin/settings">
                <input type="hidden" name="type" value="department">
                <input name="value" placeholder="New Department" required>
                <button type="submit">Add Department</button>
            </form>
        </div>
        <div class="col">
            <h3>Categories</h3>
            <ul>
                <% for (String category : categories) { %>
                <li><%= category %></li>
                <% } %>
            </ul>
            <form method="post" action="${pageContext.request.contextPath}/secure/admin/settings">
                <input type="hidden" name="type" value="category">
                <input name="value" placeholder="New Category" required>
                <button type="submit">Add Category</button>
            </form>
        </div>
    </div>
</div>
</body>
</html>
