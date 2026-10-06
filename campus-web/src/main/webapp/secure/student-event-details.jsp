<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.net.URLEncoder,com.campus.campus.web.model.Event" %>
<%
    Event event = (Event) request.getAttribute("event");
    Boolean hasReservationAttr = (Boolean) request.getAttribute("hasReservation");
    boolean hasReservation = hasReservationAttr != null && hasReservationAttr;
%>
<!DOCTYPE html>
<html>
<head>
    <title>Event Details</title>
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
        <h2>Event Details</h2>
    </div>
    <a class="btn btn-view" href="${pageContext.request.contextPath}/secure/student/dashboard"><i class="fa-solid fa-arrow-left"></i> Back to Browse</a>

    <% if (event != null) { %>
    <% if ("reserved".equals(request.getParameter("result"))) { %>
    <div class="msg">You registered successfully.</div>
    <% } else if ("cancelled".equals(request.getParameter("result"))) { %>
    <div class="msg">Your reservation was cancelled and the seat was released.</div>
    <% } else if ("failed".equals(request.getParameter("result"))) { %>
    <div class="msg err">Operation failed. Event may be full, closed, or already started.</div>
    <% } %>

    <%
        boolean bookingAllowed = !"EXPIRED".equals(event.getStatus().name()) && event.getSeatsRemaining() > 0 && !hasReservation;
        boolean cancellationAllowed = hasReservation && event.getEventDateTime() != null && event.getEventDateTime().isAfter(java.time.LocalDateTime.now());
        String smartStatus;
        if ("EXPIRED".equals(event.getStatus().name())) {
            smartStatus = "Expired";
        } else if (event.getSeatsRemaining() <= 0) {
            smartStatus = "Full";
        } else {
            smartStatus = "Active";
        }
    %>
    <div class="split-layout">
        <div class="panel">
            <h3><%= event.getTitle() %></h3>
            <p><i class="fa-solid fa-building"></i> Department/Club: <%= event.getDepartmentClub() %></p>
            <p><i class="fa-regular fa-calendar"></i> Date & Time: <%= event.getEventDateTime() %></p>
            <p><i class="fa-solid fa-location-dot"></i> Location: <%= event.getLocation() %></p>
            <p><i class="fa-solid fa-tag"></i> Category: <%= event.getCategory() %></p>
            <p><i class="fa-solid fa-shapes"></i> Type: <%= event.getEventType() %></p>
            <p><i class="fa-solid fa-users"></i> Seats: <%= event.getSeatsRemaining() %> / <%= event.getCapacity() %></p>
            <p><strong>Status:</strong> <%= smartStatus %></p>
            <p><strong>Description</strong></p>
            <p><%= event.getDescription() %></p>
        </div>
        <div class="panel">
            <% if (event.getEventImage() != null && !event.getEventImage().isBlank()) { %>
            <img class="event-image" src="${pageContext.request.contextPath}/media/event-image?name=<%= URLEncoder.encode(event.getEventImage(), "UTF-8") %>" alt="Event image">
            <% } else { %>
            <div class="upload-preview">No image available</div>
            <% } %>
        </div>
    </div>

    <div class="split-layout" style="margin-top:16px;">
        <div class="panel">
            <h3>Book Ticket</h3>
            <form method="post" action="${pageContext.request.contextPath}/secure/reservations">
                <input type="hidden" name="action" value="reserve">
                <input type="hidden" name="eventId" value="<%= event.getId() %>">
                <input type="hidden" name="redirect" value="details">
                <button class="btn-solid" type="submit" <%= bookingAllowed ? "" : "disabled" %>><%= bookingAllowed ? "Book Ticket" : "Booking Unavailable" %></button>
            </form>
            <% if (hasReservation) { %>
            <form method="post" action="${pageContext.request.contextPath}/secure/reservations" style="margin-top:8px;">
                <input type="hidden" name="action" value="cancel">
                <input type="hidden" name="eventId" value="<%= event.getId() %>">
                <input type="hidden" name="redirect" value="details">
                <button class="danger" type="submit" <%= cancellationAllowed ? "" : "disabled" %>><%= cancellationAllowed ? "Cancel Reservation" : "Cancellation Unavailable" %></button>
            </form>
            <% } %>
        </div>
        <div class="panel">
            <h3>Rate Event</h3>
            <p>Ratings are available in <strong>Attended Events & Rating</strong> after your attendance is marked PRESENT.</p>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/secure/student/attended-events">Go to Attended Events</a>
        </div>
    </div>
    <% } %>
</div>
</body>
</html>
