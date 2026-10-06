<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.net.URLEncoder,com.campus.campus.web.model.Event" %>
<%
    Event event = (Event) request.getAttribute("event");
    Integer registeredCount = (Integer) request.getAttribute("registeredCount");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Event Details</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
</head>
<body class="organizer-page">
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

    <% if (event != null) { %>
    <%
        String smartStatus;
        if ("EXPIRED".equals(event.getStatus().name())) {
            smartStatus = "Expired";
        } else if (event.isCompleted()) {
            smartStatus = "Completed";
        } else if (event.getSeatsRemaining() <= 0) {
            smartStatus = "Full";
        } else {
            smartStatus = "Active";
        }
    %>
    <div class="page-header">
        <h2><%= event.getTitle() %></h2>
        <p>Full event details and management actions.</p>
    </div>
    <div class="split-layout">
        <div class="panel">
            <p><strong>Organizer:</strong> <%= event.getOrganizerName() %></p>
            <p><strong>Date:</strong> <%= event.getEventDateTime() %></p>
            <p><strong>Location:</strong> <%= event.getLocation() %></p>
            <p><strong>Department/Club:</strong> <%= event.getDepartmentClub() %></p>
            <p><strong>Category:</strong> <%= event.getCategory() %></p>
            <p><strong>Type:</strong> <%= event.getEventType() %></p>
            <p><strong>Capacity:</strong> <%= event.getCapacity() %></p>
            <p><strong>Registered:</strong> <%= registeredCount == null ? 0 : registeredCount %></p>
            <p><strong>Status:</strong> <%= smartStatus %></p>
            <p><strong>Description:</strong></p>
            <p><%= event.getDescription() %></p>
        </div>
        <div class="panel">
            <% if (event.getEventImage() != null && !event.getEventImage().isBlank()) { %>
            <img class="event-image" src="${pageContext.request.contextPath}/media/event-image?name=<%= URLEncoder.encode(event.getEventImage(), "UTF-8") %>" alt="Event image">
            <% } else { %>
            <div class="upload-preview">No event image</div>
            <% } %>
        </div>
    </div>
    <div class="landing-actions action-row" style="justify-content:flex-start;margin-top:16px;">
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/secure/organizer/manage-events?editEventId=<%= event.getId() %>">Manage Event</a>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/secure/organizer/edit-event?eventId=<%= event.getId() %>">Edit Event</a>
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/secure/organizer/dashboard">Back</a>
    </div>
    <% } %>
</div>
</body>
</html>
