<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,com.campus.campus.web.model.Reservation" %>
<%
    List<Reservation> reservations = (List<Reservation>) request.getAttribute("reservations");
%>
<!DOCTYPE html>
<html>
<head>
    <title>My Reservations</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body>
<div class="container">
    <jsp:include page="_menu.jsp"/>
    <h2>My Reservations</h2>

    <% if ("cancelled".equals(request.getParameter("result"))) { %>
    <div class="msg">Reservation cancelled and seat released.</div>
    <% } else if ("failed".equals(request.getParameter("result"))) { %>
    <div class="msg err">Cancellation failed.</div>
    <% } else if ("rated".equals(request.getParameter("result"))) { %>
    <div class="msg">Rating submitted successfully.</div>
    <% } else if ("ratingFailed".equals(request.getParameter("result"))) { %>
    <div class="msg err">Rating value must be between 1 and 5.</div>
    <% } %>

    <table>
        <thead>
        <tr>
            <th>Event</th>
            <th>Date</th>
            <th>Status</th>
            <th>Attendance</th>
            <th>Action</th>
            <th>Rate Event</th>
        </tr>
        </thead>
        <tbody>
        <% for (Reservation reservation : reservations) { %>
        <tr>
            <td><%= reservation.getEventTitle() %></td>
            <td><%= reservation.getEventDateTime() %></td>
            <td><%= reservation.getReservationStatus() %></td>
            <td><%= reservation.getAttendanceStatus() %></td>
            <td>
                <form method="post" action="${pageContext.request.contextPath}/secure/reservations">
                    <input type="hidden" name="action" value="cancel">
                    <input type="hidden" name="eventId" value="<%= reservation.getEventId() %>">
                    <button class="danger" type="submit">Cancel</button>
                </form>
            </td>
            <td>
                <form method="post" action="${pageContext.request.contextPath}/secure/ratings">
                    <input type="hidden" name="eventId" value="<%= reservation.getEventId() %>">
                    <input type="number" min="1" max="5" name="rating" placeholder="1-5" required>
                    <input name="feedback" placeholder="Optional feedback">
                    <button type="submit">Submit Rating</button>
                </form>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>
</div>
</body>
</html>
