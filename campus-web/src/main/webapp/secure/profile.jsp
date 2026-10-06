<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.campus.campus.web.model.User" %>
<%
    User user = (User) session.getAttribute("user");
%>
<!DOCTYPE html>
<html>
<head>
    <title>My Profile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
</head>
<body class="profile-page">
<div class="container page-enter">
    <jsp:include page="_menu.jsp"/>
    <div class="page-header">
        <h2><i class="fa-regular fa-user"></i> My Profile</h2>
        <p>Update your personal information and keep your account secure.</p>
    </div>
    <% if (request.getParameter("success") != null) { %>
    <div class="msg">Profile updated successfully.</div>
    <% } else if ("emailExists".equals(request.getParameter("error"))) { %>
    <div class="msg err">This email is already used by another account.</div>
    <% } else if ("missing".equals(request.getParameter("error"))) { %>
    <div class="msg err">Please fill all required fields.</div>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/secure/profile" class="form-grid panel profile-form">
        <div class="input-icon-wrap">
            <i class="fa-regular fa-id-badge"></i>
            <input name="fullName" value="<%= user.getFullName() %>" placeholder="Full Name" required>
        </div>
        <div class="input-icon-wrap">
            <i class="fa-regular fa-envelope"></i>
            <input type="email" name="email" value="<%= user.getEmail() %>" placeholder="Email" required>
        </div>
        <div class="input-icon-wrap">
            <i class="fa-solid fa-building-columns"></i>
            <input name="faculty" value="<%= user.getFaculty() %>" placeholder="College / Faculty" required>
        </div>
        <div class="input-icon-wrap">
            <i class="fa-solid fa-diagram-project"></i>
            <input name="department" value="<%= user.getDepartment() %>" placeholder="Department / Specialization" required>
        </div>
        <div class="input-icon-wrap">
            <i class="fa-regular fa-calendar"></i>
            <input type="number" name="admissionYear" value="<%= user.getAdmissionYear() %>" placeholder="Admission Year" required>
        </div>
        <div class="input-icon-wrap">
            <i class="fa-solid fa-lock"></i>
            <input type="password" name="newPassword" placeholder="New Password (leave blank to keep current)">
        </div>
        <button class="btn-solid full" type="submit">Save Profile</button>
    </form>
</div>
</body>
</html>
