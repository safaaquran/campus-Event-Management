<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,java.util.Map,com.campus.campus.web.model.Event" %>
<%
    List<Event> events = (List<Event>) request.getAttribute("events");
    Map<Long, Integer> attendeeCounts = (Map<Long, Integer>) request.getAttribute("attendeeCounts");
    String editEventId = request.getAttribute("editEventId") == null ? null : request.getAttribute("editEventId").toString();
%>
<!DOCTYPE html>
<html>
<head>
    <title>Manage Events</title>
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
        <h2>Manage Events</h2>
        <p>Edit event details, view registered attendees, and control event lifecycle.</p>
    </div>
    <div class="landing-actions" style="justify-content:flex-start;">
        <a class="btn btn-view" href="${pageContext.request.contextPath}/secure/organizer/dashboard"><i class="fa-solid fa-arrow-left"></i> Back</a>
    </div>

    <% if ("created".equals(request.getParameter("result"))) { %>
    <div class="msg">Event created successfully.</div>
    <% } else if ("updated".equals(request.getParameter("result"))) { %>
    <div class="msg">Event updated successfully.</div>
    <% } else if ("deleted".equals(request.getParameter("result"))) { %>
    <div class="msg">Event deleted successfully.</div>
    <% } else if ("closed".equals(request.getParameter("result"))) { %>
    <div class="msg">Registration closed successfully.</div>
    <% } else if ("completed".equals(request.getParameter("result"))) { %>
    <div class="msg">Event marked as completed.</div>
    <% } %>

    <div class="toolbar-grid">
        <div class="search-wrap">
            <i class="fa-solid fa-magnifying-glass"></i>
            <input id="manageSearch" type="text" placeholder="Search by event title">
        </div>
        <select id="manageStatusFilter">
            <option value="">All Statuses</option>
            <option value="ACTIVE">Active</option>
            <option value="FULL">Full</option>
            <option value="COMPLETED">Completed</option>
            <option value="EXPIRED">Expired</option>
        </select>
    </div>

    <% if (events == null || events.isEmpty()) { %>
    <div class="empty-state">
        <p>No events yet</p>
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/secure/organizer/dashboard">Go to Dashboard</a>
    </div>
    <% } else { %>
    <div id="manageEventCards" class="organizer-events-grid">
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
            int registered = attendeeCounts.getOrDefault(event.getId(), 0);
        %>
        <div class="event-card compact <%= String.valueOf(event.getId()).equals(editEventId) ? "highlight-card" : "" %>"
             data-title="<%= event.getTitle().toLowerCase() %>"
             data-status="<%= smartStatus.toUpperCase() %>">
            <div class="event-content">
                <h3><%= event.getTitle() %></h3>
                <p><i class="fa-regular fa-calendar"></i> <%= event.getEventDateTime() %></p>
                <p><i class="fa-solid fa-location-dot"></i> <%= event.getLocation() %></p>
                <p><i class="fa-solid fa-users"></i> Capacity: <%= event.getCapacity() %> / Registered: <%= registered %></p>
                <span class="status-badge <%= badgeClass %>"><%= smartStatus %></span>

                <div class="card-actions">
                <a class="btn btn-sm btn-edit" href="${pageContext.request.contextPath}/secure/organizer/edit-event?eventId=<%= event.getId() %>">Edit</a>
                <a class="btn btn-sm btn-view" href="${pageContext.request.contextPath}/secure/organizer/event-details?eventId=<%= event.getId() %>">View Details</a>
                <a class="btn btn-sm btn-view" href="${pageContext.request.contextPath}/secure/organizer/attendance?eventId=<%= event.getId() %>">Attendance</a>
                <form class="inline-form" method="post" action="${pageContext.request.contextPath}/secure/organizer/manage-events">
                    <input type="hidden" name="action" value="close">
                    <input type="hidden" name="eventId" value="<%= event.getId() %>">
                    <button class="warn" type="submit">Close Registration</button>
                </form>
                <form class="inline-form" method="post" action="${pageContext.request.contextPath}/secure/organizer/manage-events">
                    <input type="hidden" name="action" value="complete">
                    <input type="hidden" name="eventId" value="<%= event.getId() %>">
                    <button type="submit">Mark Completed</button>
                </form>
                <form class="inline-form" method="post" action="${pageContext.request.contextPath}/secure/organizer/manage-events"
                      onsubmit="return confirm('Are you sure you want to delete this event?');">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="eventId" value="<%= event.getId() %>">
                    <button class="danger" type="submit">Delete</button>
                </form>
                </div>
            </div>
        </div>
        <% } %>
    </div>
    <% } %>
</div>

<script>
    const manageSearchInput = document.getElementById('manageSearch');
    const manageStatusFilter = document.getElementById('manageStatusFilter');
    const manageCards = Array.from(document.querySelectorAll('#manageEventCards .event-card'));

    function applyManageFilters() {
        const q = (manageSearchInput.value || '').trim().toLowerCase();
        const status = (manageStatusFilter.value || '').trim().toUpperCase();
        manageCards.forEach(function (card) {
            const title = card.dataset.title || '';
            const cardStatus = card.dataset.status || '';
            const matchQ = !q || title.includes(q);
            const matchStatus = !status || cardStatus === status;
            card.style.display = (matchQ && matchStatus) ? '' : 'none';
        });
    }

    if (manageSearchInput && manageStatusFilter) {
        manageSearchInput.addEventListener('input', applyManageFilters);
        manageStatusFilter.addEventListener('change', applyManageFilters);
    }
</script>
</body>
</html>
