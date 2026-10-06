<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,java.util.Map,java.net.URLEncoder,com.campus.campus.web.model.Event" %>
<%
    List<Event> events = (List<Event>) request.getAttribute("events");
    Map<Long, Integer> attendeeCounts = (Map<Long, Integer>) request.getAttribute("attendeeCounts");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Organizer Dashboard</title>
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

    <div class="page-header">
        <h2>My Events</h2>
        <p>Manage and create your events easily.</p>
    </div>

    <div class="landing-actions action-row" style="justify-content:flex-start;">
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/secure/organizer/create-event"><i class="fa-solid fa-plus"></i> Create Event</a>
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/secure/organizer/manage-events"><i class="fa-solid fa-gear"></i> Manage Events</a>
    </div>

    <form method="get" action="${pageContext.request.contextPath}/secure/organizer/dashboard" class="toolbar-grid">
        <select name="status">
            <option value="ALL">All Statuses</option>
            <option value="ACTIVE" <%= "ACTIVE".equals(request.getParameter("status")) ? "selected" : "" %>>Active</option>
            <option value="FULL" <%= "FULL".equals(request.getParameter("status")) ? "selected" : "" %>>Full</option>
            <option value="COMPLETED" <%= "COMPLETED".equals(request.getParameter("status")) ? "selected" : "" %>>Completed</option>
            <option value="EXPIRED" <%= "EXPIRED".equals(request.getParameter("status")) ? "selected" : "" %>>Expired</option>
        </select>
        <input type="date" name="date" value="<%= request.getParameter("date") == null ? "" : request.getParameter("date") %>">
        <button class="btn-solid" type="submit">Filter</button>
    </form>

    <div class="organizer-events-grid">
        <% if (events == null || events.isEmpty()) { %>
        <div class="empty-state">
            <p>No events yet</p>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/secure/organizer/create-event">Create Your First Event</a>
        </div>
        <% } else { %>
        <% for (Event event : events) { %>
        <%
            String smartStatus;
            String badgeClass;
            if ("EXPIRED".equals(event.getStatus().name())) {
                smartStatus = "Expired";
                badgeClass = "badge-expired";
            } else if (event.isCompleted()) {
                smartStatus = "Completed";
                badgeClass = "badge-completed";
            } else if (event.getSeatsRemaining() <= 0) {
                smartStatus = "Full";
                badgeClass = "badge-full";
            } else {
                smartStatus = "Active";
                badgeClass = "badge-active";
            }
            int registered = attendeeCounts == null ? 0 : attendeeCounts.getOrDefault(event.getId(), 0);
        %>
        <div class="event-card compact">
            <div class="event-image-wrap small">
                <% if (event.getEventImage() != null && !event.getEventImage().isBlank()) { %>
                <img class="event-image" src="${pageContext.request.contextPath}/media/event-image?name=<%= URLEncoder.encode(event.getEventImage(), "UTF-8") %>" alt="Event image">
                <% } else { %>
                <div class="event-image placeholder"><i class="fa-regular fa-image"></i></div>
                <% } %>
            </div>
            <div class="event-content">
                <h3><%= event.getTitle() %></h3>
                <p><i class="fa-regular fa-calendar"></i> <%= event.getEventDateTime() %></p>
                <p><i class="fa-solid fa-location-dot"></i> <%= event.getLocation() %></p>
                <p><i class="fa-solid fa-users"></i> Capacity: <%= event.getCapacity() %> / Registered: <%= registered %></p>
                <span class="status-badge <%= badgeClass %>"><%= smartStatus %></span>

                <div class="card-actions">
                    <a class="btn btn-sm btn-edit" href="${pageContext.request.contextPath}/secure/organizer/edit-event?eventId=<%= event.getId() %>">Edit</a>
                    <a class="btn btn-sm btn-view" href="${pageContext.request.contextPath}/secure/organizer/event-details?eventId=<%= event.getId() %>">View Details</a>
                    <form class="inline-form" method="post" action="${pageContext.request.contextPath}/secure/organizer/manage-events"
                          onsubmit="return confirm('Are you sure you want to delete this event?');">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="eventId" value="<%= event.getId() %>">
                        <button class="danger btn-sm" type="submit">Delete</button>
                    </form>
                </div>
            </div>
        </div>
        <% } %>
        <% } %>
    </div>
</div>
</body>
</html>
