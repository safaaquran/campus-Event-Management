<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,com.campus.campus.web.model.Event" %>
<%
    List<Event> events = (List<Event>) request.getAttribute("events");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Organizer Events</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body>
<div class="container">
    <jsp:include page="_menu.jsp"/>
    <h2>Organizer Event Management</h2>

    <h3>Create Event</h3>
    <form method="post" action="${pageContext.request.contextPath}/secure/organizer/events" enctype="multipart/form-data">
        <input type="hidden" name="action" value="create">
        <input name="title" placeholder="Event Title" required>
        <textarea name="description" placeholder="Description" required></textarea>
        <input name="departmentClub" placeholder="Department/Club" required>
        <input type="datetime-local" name="eventDateTime" required>
        <input name="location" placeholder="Location" required>
        <input type="number" min="1" name="capacity" placeholder="Capacity" required>
        <select name="category" required>
            <option value="EDUCATIONAL">Educational</option>
            <option value="SOCIAL">Social</option>
            <option value="SPORTS">Sports</option>
            <option value="CULTURAL">Cultural</option>
            <option value="TECHNICAL">Technical</option>
        </select>
        <select name="eventType" required>
            <option value="WORKSHOP">Workshop</option>
            <option value="SEMINAR">Seminar</option>
            <option value="CLUB_SOCIAL_EVENT">Club Social Event</option>
            <option value="SPORTS_ACTIVITY">Sports Activity</option>
        </select>
        <input type="file" name="eventImage" accept="image/*">
        <button type="submit">Create Event</button>
    </form>

    <h3>My Events</h3>
    <table>
        <thead>
        <tr>
            <th>Title</th>
            <th>Date</th>
            <th>Status</th>
            <th>Seats</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <% for (Event event : events) { %>
        <tr>
            <td><%= event.getTitle() %></td>
            <td><%= event.getEventDateTime() %></td>
            <td><%= event.getStatus() %> / Completed: <%= event.isCompleted() %></td>
            <td><%= event.getSeatsRemaining() %> / <%= event.getCapacity() %></td>
            <td>
                <form method="post" action="${pageContext.request.contextPath}/secure/organizer/events" enctype="multipart/form-data">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="eventId" value="<%= event.getId() %>">
                    <input name="title" value="<%= event.getTitle() %>" required>
                    <textarea name="description" required><%= event.getDescription() %></textarea>
                    <input name="departmentClub" value="<%= event.getDepartmentClub() %>" required>
                    <input type="datetime-local" name="eventDateTime" value="<%= event.getEventDateTime().toString().substring(0, 16) %>" required>
                    <input name="location" value="<%= event.getLocation() %>" required>
                    <input type="number" min="1" name="capacity" value="<%= event.getCapacity() %>" required>
                    <select name="category" required>
                        <option value="EDUCATIONAL">Educational</option>
                        <option value="SOCIAL">Social</option>
                        <option value="SPORTS">Sports</option>
                        <option value="CULTURAL">Cultural</option>
                        <option value="TECHNICAL">Technical</option>
                    </select>
                    <input type="file" name="eventImage" accept="image/*">
                    <button type="submit">Update Event</button>
                </form>
                <form method="post" action="${pageContext.request.contextPath}/secure/organizer/events">
                    <input type="hidden" name="action" value="close">
                    <input type="hidden" name="eventId" value="<%= event.getId() %>">
                    <button class="warn" type="submit">Close Registration</button>
                </form>
                <form method="post" action="${pageContext.request.contextPath}/secure/organizer/events">
                    <input type="hidden" name="action" value="complete">
                    <input type="hidden" name="eventId" value="<%= event.getId() %>">
                    <button type="submit">Mark Completed</button>
                </form>
                <form method="get" action="${pageContext.request.contextPath}/secure/organizer/attendance">
                    <input type="hidden" name="eventId" value="<%= event.getId() %>">
                    <button type="submit">Attendance</button>
                </form>
                <form method="post" action="${pageContext.request.contextPath}/secure/organizer/events">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="eventId" value="<%= event.getId() %>">
                    <button class="danger" type="submit">Delete</button>
                </form>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>
</div>
</body>
</html>
