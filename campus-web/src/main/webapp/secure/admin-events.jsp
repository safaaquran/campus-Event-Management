<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,com.campus.campus.web.model.Event,com.campus.campus.web.model.EventStatus" %>
<%
    List<Event> events = (List<Event>) request.getAttribute("events");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Events</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
</head>
<body>
<div class="container page-enter">
    <div class="dashboard-topbar">
        <div class="topbar-brand">
            <i class="fa-solid fa-shield-halved"></i>
            Campus Admin
        </div>
        <div class="topbar-actions">
            <a href="${pageContext.request.contextPath}/secure/profile"><i class="fa-regular fa-user"></i> Profile</a>
            <a href="${pageContext.request.contextPath}/logout"><i class="fa-solid fa-right-from-bracket"></i> Logout</a>
        </div>
    </div>
    <h2>Event Dashboard</h2>

    <div class="toolbar-grid">
        <div class="search-wrap">
            <i class="fa-solid fa-magnifying-glass"></i>
            <input id="adminEventSearch" type="text" placeholder="Search by event title or organizer">
        </div>
        <select id="adminEventStatusFilter">
            <option value="">All Statuses</option>
            <option value="OPEN">Open</option>
            <option value="CLOSED">Closed</option>
            <option value="EXPIRED">Expired</option>
        </select>
    </div>

    <div id="adminEventCards" class="admin-user-grid">
        <% for (Event event : events) { %>
        <div class="admin-user-card" data-title="<%= event.getTitle().toLowerCase() %>" data-organizer="<%= event.getOrganizerName().toLowerCase() %>" data-status="<%= event.getStatus().name() %>">
            <div class="admin-user-header">
                <h3><i class="fa-regular fa-calendar"></i> <%= event.getTitle() %></h3>
                <span class="status-badge <%= event.getStatus() == EventStatus.OPEN ? "badge-active" : (event.getStatus() == EventStatus.CLOSED ? "badge-full" : "badge-expired") %>">
                    <%= event.getStatus() %>
                </span>
            </div>
            <p><strong>Organizer:</strong> <%= event.getOrganizerName() %></p>
            <p><strong>Date:</strong> <%= event.getEventDateTime() %></p>
            <p><strong>Location:</strong> <%= event.getLocation() %></p>
            <p><strong>Capacity:</strong> <%= event.getCapacity() %></p>

            <details class="admin-user-details">
                <summary>Edit Event</summary>
                <form method="post" action="${pageContext.request.contextPath}/secure/admin/events" class="form-grid">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="eventId" value="<%= event.getId() %>">
                    <input name="title" value="<%= event.getTitle() %>" required>
                    <input name="location" value="<%= event.getLocation() %>" required>
                    <input type="datetime-local" name="eventDateTime" value="<%= event.getEventDateTime().toString().substring(0, 16) %>" required>
                    <input type="number" min="1" name="capacity" value="<%= event.getCapacity() %>" required>
                    <button class="btn-solid full" type="submit">Save Changes</button>
                </form>
            </details>

            <div class="card-actions">
                <form class="inline-form" method="post" action="${pageContext.request.contextPath}/secure/admin/events">
                    <input type="hidden" name="eventId" value="<%= event.getId() %>">
                    <% if (event.getStatus() == EventStatus.OPEN) { %>
                    <input type="hidden" name="action" value="close">
                    <button class="warn btn-sm" type="submit">Close</button>
                    <% } else { %>
                    <input type="hidden" name="action" value="open">
                    <button class="btn btn-sm btn-edit" type="submit">Open</button>
                    <% } %>
                </form>
                <form class="inline-form" method="post" action="${pageContext.request.contextPath}/secure/admin/events"
                      onsubmit="return confirm('Delete this event?');">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="eventId" value="<%= event.getId() %>">
                    <button class="danger btn-sm" type="submit">Delete</button>
                </form>
            </div>
        </div>
        <% } %>
    </div>
</div>

<script>
    const eventSearchInput = document.getElementById('adminEventSearch');
    const eventStatusFilter = document.getElementById('adminEventStatusFilter');
    const eventCards = Array.from(document.querySelectorAll('.admin-user-card'));

    function applyEventFilters() {
        const q = (eventSearchInput.value || '').trim().toLowerCase();
        const status = eventStatusFilter.value;
        eventCards.forEach(function (card) {
            const title = card.dataset.title || '';
            const organizer = card.dataset.organizer || '';
            const cardStatus = card.dataset.status || '';
            const matchSearch = !q || title.includes(q) || organizer.includes(q);
            const matchStatus = !status || cardStatus === status;
            card.style.display = (matchSearch && matchStatus) ? '' : 'none';
        });
    }

    eventSearchInput.addEventListener('input', applyEventFilters);
    eventStatusFilter.addEventListener('change', applyEventFilters);
</script>
</body>
</html>
