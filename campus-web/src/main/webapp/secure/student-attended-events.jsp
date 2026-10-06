<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,com.campus.campus.web.model.Reservation" %>
<%
    List<Reservation> reservations = (List<Reservation>) request.getAttribute("reservations");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Attended Events</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
</head>
<body class="student-page">
<div class="container page-enter">
    <div class="dashboard-topbar">
        <div class="topbar-brand">
            <i class="fa-solid fa-calendar-check"></i>
            Campus Events
        </div>
        <div class="topbar-actions">
            <a href="${pageContext.request.contextPath}/secure/profile"><i class="fa-regular fa-user"></i> Profile</a>
            <a href="${pageContext.request.contextPath}/logout"><i class="fa-solid fa-right-from-bracket"></i> Logout</a>
        </div>
    </div>

    <div class="page-header">
        <h2>Attended Events</h2>
        <p>Review your completed events and submit ratings easily.</p>
    </div>
    <div class="landing-actions action-row" style="justify-content:flex-start;">
        <a class="btn btn-view" href="${pageContext.request.contextPath}/secure/student/dashboard"><i class="fa-solid fa-arrow-left"></i> Back to Dashboard</a>
    </div>

    <% if ("rated".equals(request.getParameter("result"))) { %>
    <div class="msg">Rating submitted successfully.</div>
    <% } else if ("ratingFailed".equals(request.getParameter("result"))) { %>
    <div class="msg err">Rating failed. You can rate only past events where your attendance is marked PRESENT.</div>
    <% } %>

    <% if (reservations == null || reservations.isEmpty()) { %>
    <div class="empty-state">
        <p>No attended events available yet.</p>
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/secure/student/dashboard">Browse Upcoming Events</a>
    </div>
    <% } else { %>
    <div class="event-grid">
        <% for (Reservation reservation : reservations) { %>
        <div class="event-card">
            <div class="event-content">
                <h3><%= reservation.getEventTitle() %></h3>
                <p><i class="fa-regular fa-calendar"></i> <%= reservation.getEventDateTime() %></p>
                <p><i class="fa-solid fa-clipboard-check"></i> Attendance: <%= reservation.getAttendanceStatus() %></p>
                <form method="post" action="${pageContext.request.contextPath}/secure/ratings" class="attended-rating-form">
                    <input type="hidden" name="eventId" value="<%= reservation.getEventId() %>">
                    <input type="hidden" name="redirect" value="attended">
                    <div class="attended-rating-row">
                        <input type="number" min="1" max="5" name="rating" placeholder="Rating (1-5)" required>
                        <button class="btn-solid" type="submit">Submit</button>
                    </div>
                    <textarea name="feedback" placeholder="Write feedback (optional)"></textarea>
                </form>
            </div>
        </div>
        <% } %>
    </div>
    <% } %>
</div>
</body>
</html>
