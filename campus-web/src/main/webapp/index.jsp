<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Campus Event Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
</head>
<body>
<div class="landing-shell">
    <div class="landing page-enter">
        <h1>Campus Event Management & Ticketing System</h1>
        <div class="icon-grid">
            <div class="icon-card">
                <span class="icon"><i class="fa-regular fa-calendar"></i></span>
                Event Discovery
            </div>
            <div class="icon-card">
                <span class="icon"><i class="fa-solid fa-ticket"></i></span>
                Smart Ticketing
            </div>
            <div class="icon-card">
                <span class="icon"><i class="fa-solid fa-users"></i></span>
                Role-Based Access
            </div>
            <div class="icon-card">
                <span class="icon"><i class="fa-regular fa-circle-check"></i></span>
                Attendance Tracking
            </div>
        </div>
        <div class="landing-actions">
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/login.jsp">Login</a>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/signup.jsp">Sign Up</a>
        </div>
    </div>
</div>
</body>
</html>
