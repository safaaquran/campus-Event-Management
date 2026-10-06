<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,com.campus.campus.web.model.Event" %>
<%
    List<Event> events = (List<Event>) request.getAttribute("events");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Browse Events</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body>
<div class="container">
    <jsp:include page="_menu.jsp"/>
    <h2>Events</h2>

    <% if ("reserved".equals(request.getParameter("result"))) { %>
    <div class="msg">Ticket reserved successfully.</div>
    <% } else if ("failed".equals(request.getParameter("result"))) { %>
    <div class="msg err">Reservation failed (event full/closed or already reserved).</div>
    <% } %>

    <form method="get" action="${pageContext.request.contextPath}/secure/events">
        <div class="row">
            <div class="col">
                <select name="filter">
                    <option value="">No Filter</option>
                    <option value="TITLE">Title</option>
                    <option value="DEPARTMENT">Department/Club</option>
                    <option value="DATE">Date (yyyy-mm-dd)</option>
                    <option value="CATEGORY">Category enum value</option>
                    <option value="TYPE">Type enum value</option>
                    <option value="AVAILABILITY">Availability</option>
                </select>
            </div>
            <div class="col"><input name="query" placeholder="Search query"></div>
            <div class="col"><button type="submit">Search</button></div>
        </div>
    </form>

    <table>
        <thead>
        <tr>
            <th>Title</th>
            <th>Organizer</th>
            <th>Date</th>
            <th>Type</th>
            <th>Category</th>
            <th>Seats</th>
            <th>Status</th>
            <th>Avg Rating</th>
            <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <% for (Event event : events) { %>
        <tr>
            <td><%= event.getTitle() %></td>
            <td><%= event.getOrganizerName() %></td>
            <td><%= event.getEventDateTime() %></td>
            <td><%= event.getEventType() %></td>
            <td><%= event.getCategory() %></td>
            <td><%= event.getSeatsRemaining() %> / <%= event.getCapacity() %></td>
            <td><%= event.getStatus() %></td>
            <td><%= String.format("%.1f", event.getAverageRating()) %> / 5</td>
            <td>
                <form method="post" action="${pageContext.request.contextPath}/secure/reservations">
                    <input type="hidden" name="action" value="reserve">
                    <input type="hidden" name="eventId" value="<%= event.getId() %>">
                    <button type="submit">Reserve Ticket</button>
                </form>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>
</div>
</body>
</html>
