<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,java.net.URLEncoder,com.campus.campus.web.model.Event" %>
<%
    List<Event> events = (List<Event>) request.getAttribute("events");
    List<String> departments = (List<String>) request.getAttribute("departments");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Browse Events</title>
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
        <h2>Browse Events</h2>
        <p>Discover and join events easily.</p>
    </div>
    <div class="landing-actions action-row" style="justify-content:flex-start;">
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/secure/student/attended-events"><i class="fa-solid fa-clock-rotate-left"></i> Attended Events & Rating</a>
    </div>

    <% if ("reserved".equals(request.getParameter("result"))) { %>
    <div class="msg">You registered successfully.</div>
    <% } else if ("failed".equals(request.getParameter("result"))) { %>
    <div class="msg err">Event is full or registration is closed.</div>
    <% } %>

    <form method="get" action="${pageContext.request.contextPath}/secure/student/dashboard" class="toolbar-grid">
        <div class="search-wrap">
            <i class="fa-solid fa-magnifying-glass"></i>
            <input type="text" name="q" value="<%= request.getParameter("q") == null ? "" : request.getParameter("q") %>" placeholder="Search events by title">
        </div>
        <select name="category">
            <option value="">All Categories</option>
            <option value="EDUCATIONAL" <%= "EDUCATIONAL".equals(request.getParameter("category")) ? "selected" : "" %>>Educational</option>
            <option value="SOCIAL" <%= "SOCIAL".equals(request.getParameter("category")) ? "selected" : "" %>>Social</option>
            <option value="SPORTS" <%= "SPORTS".equals(request.getParameter("category")) ? "selected" : "" %>>Sports</option>
            <option value="CULTURAL" <%= "CULTURAL".equals(request.getParameter("category")) ? "selected" : "" %>>Cultural</option>
            <option value="TECHNICAL" <%= "TECHNICAL".equals(request.getParameter("category")) ? "selected" : "" %>>Technical</option>
        </select>
        <select name="type">
            <option value="">All Types</option>
            <option value="WORKSHOP" <%= "WORKSHOP".equals(request.getParameter("type")) ? "selected" : "" %>>Workshop</option>
            <option value="SEMINAR" <%= "SEMINAR".equals(request.getParameter("type")) ? "selected" : "" %>>Seminar</option>
            <option value="CLUB_SOCIAL_EVENT" <%= "CLUB_SOCIAL_EVENT".equals(request.getParameter("type")) ? "selected" : "" %>>Club Social Event</option>
            <option value="SPORTS_ACTIVITY" <%= "SPORTS_ACTIVITY".equals(request.getParameter("type")) ? "selected" : "" %>>Sports Activity</option>
        </select>
        <select name="department">
            <option value="">All Departments</option>
            <% if (departments != null) { %>
            <% for (String dept : departments) { %>
            <option value="<%= dept %>" <%= dept.equals(request.getParameter("department")) ? "selected" : "" %>><%= dept %></option>
            <% } %>
            <% } %>
        </select>
        <input type="date" name="date" value="<%= request.getParameter("date") == null ? "" : request.getParameter("date") %>">
        <input type="time" name="time" value="<%= request.getParameter("time") == null ? "" : request.getParameter("time") %>">
        <button class="btn-solid" type="submit">Apply</button>
    </form>

    <div class="event-grid">
        <% if (events == null || events.isEmpty()) { %>
        <div class="empty-state">No events available</div>
        <% } else { %>
        <% for (Event event : events) { %>
        <%
            String smartStatus;
            String badgeClass;
            if ("EXPIRED".equals(event.getStatus().name())) {
                smartStatus = "Expired";
                badgeClass = "badge-expired";
            } else if (event.getSeatsRemaining() <= 0) {
                smartStatus = "Full";
                badgeClass = "badge-full";
            } else {
                smartStatus = "Active";
                badgeClass = "badge-active";
            }
        %>
        <div class="event-card">
            <div class="event-image-wrap">
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
                <p><i class="fa-solid fa-building"></i> <%= event.getDepartmentClub() %></p>
                <span class="status-badge <%= badgeClass %>"><%= smartStatus %></span>
                <form method="get" action="${pageContext.request.contextPath}/secure/student/event-details">
                    <input type="hidden" name="eventId" value="<%= event.getId() %>">
                    <button class="btn-solid" type="submit">View Details</button>
                </form>
            </div>
        </div>
        <% } %>
        <% } %>
    </div>
</div>

<script>
    const filterForm = document.querySelector('.toolbar-grid');
    const filterInputs = filterForm ? filterForm.querySelectorAll('select, input[type="date"], input[type="time"]') : [];
    filterInputs.forEach(function (el) {
        el.addEventListener('change', function () {
            filterForm.submit();
        });
    });

    const searchInput = filterForm ? filterForm.querySelector('input[name="q"]') : null;
    if (searchInput) {
        let timer = null;
        searchInput.addEventListener('input', function () {
            if (timer) clearTimeout(timer);
            timer = setTimeout(function () {
                filterForm.submit();
            }, 500);
        });
    }
</script>
</body>
</html>
