<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,com.campus.campus.web.model.Reservation" %>
<%
    List<Reservation> reservations = (List<Reservation>) request.getAttribute("reservations");
    long eventId = (Long) request.getAttribute("eventId");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Attendance</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body class="organizer-page">
<div class="container page-enter">
    <div class="dashboard-topbar">
        <div class="topbar-brand">Campus Events</div>
        <div class="topbar-actions">
            <a href="${pageContext.request.contextPath}/secure/profile">Profile</a>
            <a href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </div>
    <div class="page-header">
        <h2>Attendance Marking</h2>
        <p>Mark each attendee as Present or Absent.</p>
    </div>
    <div class="landing-actions" style="justify-content:flex-start;">
        <a class="btn btn-view" href="${pageContext.request.contextPath}/secure/organizer/manage-events">Back to Manage Events</a>
    </div>

    <% if (reservations == null || reservations.isEmpty()) { %>
    <div class="empty-state">No reservations found for this event yet.</div>
    <% } else { %>
    <div class="attendance-list">
        <% for (Reservation reservation : reservations) { %>
        <div class="attendance-card">
            <div class="attendance-meta">
                <h3><%= (reservation.getStudentName() == null || reservation.getStudentName().isBlank()) ? "Student" : reservation.getStudentName() %></h3>
                <p>ID: <%= reservation.getStudentId() %> | Reservation #<%= reservation.getId() %></p>
                <span class="status-badge <%= "PRESENT".equals(String.valueOf(reservation.getAttendanceStatus())) ? "badge-active" : "badge-full" %>">
                    <%= reservation.getAttendanceStatus() %>
                </span>
            </div>
            <form method="post" action="${pageContext.request.contextPath}/secure/organizer/attendance" class="attendance-form">
                <input type="hidden" name="reservationId" value="<%= reservation.getId() %>">
                <input type="hidden" name="eventId" value="<%= eventId %>">
                <div class="attendance-actions">
                    <select name="attendanceStatus">
                        <option value="PRESENT" <%= "PRESENT".equals(String.valueOf(reservation.getAttendanceStatus())) ? "selected" : "" %>>Present</option>
                        <option value="ABSENT" <%= "ABSENT".equals(String.valueOf(reservation.getAttendanceStatus())) ? "selected" : "" %>>Absent</option>
                    </select>
                    <button class="btn-solid" type="submit">Update</button>
                </div>
            </form>
        </div>
        <% } %>
    </div>
    <% } %>
</div>
</body>
</html>
