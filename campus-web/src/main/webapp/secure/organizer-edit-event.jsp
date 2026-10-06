<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.net.URLEncoder,java.util.List,com.campus.campus.web.model.Event,com.campus.campus.web.model.EventCategory" %>
<%
    Event event = (Event) request.getAttribute("event");
    List<String> departments = (List<String>) request.getAttribute("departments");
    List<String> categories = (List<String>) request.getAttribute("categories");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Event</title>
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
        <h2>Edit Event</h2>
        <p>Update event details and save changes.</p>
    </div>

    <% if (event != null) { %>
    <form method="post" action="${pageContext.request.contextPath}/secure/organizer/edit-event" enctype="multipart/form-data" class="split-layout">
        <input type="hidden" name="eventId" value="<%= event.getId() %>">
        <div class="panel form-grid">
            <input class="full" name="title" value="<%= event.getTitle() %>" required>
            <input name="organizerName" value="<%= event.getOrganizerName() %>" required>
            <select name="departmentClub" required>
                <% if (departments != null && !departments.isEmpty()) { %>
                    <% for (String department : departments) { %>
                    <option value="<%= department %>" <%= department.equals(event.getDepartmentClub()) ? "selected" : "" %>><%= department %></option>
                    <% } %>
                <% } else { %>
                    <option value="Engineering Club" <%= "Engineering Club".equals(event.getDepartmentClub()) ? "selected" : "" %>>Engineering Club</option>
                    <option value="Medical Students Club" <%= "Medical Students Club".equals(event.getDepartmentClub()) ? "selected" : "" %>>Medical Students Club</option>
                    <option value="IT Innovation Club" <%= "IT Innovation Club".equals(event.getDepartmentClub()) ? "selected" : "" %>>IT Innovation Club</option>
                    <option value="Agriculture Club" <%= "Agriculture Club".equals(event.getDepartmentClub()) ? "selected" : "" %>>Agriculture Club</option>
                    <option value="Science Society" <%= "Science Society".equals(event.getDepartmentClub()) ? "selected" : "" %>>Science Society</option>
                    <option value="Arts and Culture Club" <%= "Arts and Culture Club".equals(event.getDepartmentClub()) ? "selected" : "" %>>Arts and Culture Club</option>
                    <option value="Pharmacy Club" <%= "Pharmacy Club".equals(event.getDepartmentClub()) ? "selected" : "" %>>Pharmacy Club</option>
                    <option value="Nursing Club" <%= "Nursing Club".equals(event.getDepartmentClub()) ? "selected" : "" %>>Nursing Club</option>
                <% } %>
            </select>

            <textarea class="full" name="description" required><%= event.getDescription() %></textarea>
            <input type="datetime-local" name="eventDateTime" value="<%= event.getEventDateTime().toString().substring(0, 16) %>" required>
            <select name="location" required>
                <option value="NG" <%= "NG".equals(event.getLocation()) ? "selected" : "" %>>NG</option>
                <option value="NB" <%= "NB".equals(event.getLocation()) ? "selected" : "" %>>NB</option>
                <option value="NF" <%= "NF".equals(event.getLocation()) ? "selected" : "" %>>NF</option>
                <option value="SA" <%= "SA".equals(event.getLocation()) ? "selected" : "" %>>SA</option>
                <option value="N2" <%= "N2".equals(event.getLocation()) ? "selected" : "" %>>N2</option>
                <option value="IBN SINA HALL" <%= "IBN SINA HALL".equals(event.getLocation()) ? "selected" : "" %>>IBN SINA HALL</option>
                <option value="SALAH ALDEEN HALL" <%= "SALAH ALDEEN HALL".equals(event.getLocation()) ? "selected" : "" %>>SALAH ALDEEN HALL</option>
                <option value="LIBRARY" <%= "LIBRARY".equals(event.getLocation()) ? "selected" : "" %>>LIBRARY</option>
                <option value="M4" <%= "M4".equals(event.getLocation()) ? "selected" : "" %>>M4</option>
            </select>

            <input type="number" min="1" name="capacity" value="<%= event.getCapacity() %>" required>
            <select name="category" required>
                <% if (categories != null && !categories.isEmpty()) { %>
                    <% for (String category : categories) {
                        String normalized = category == null ? "" : category.trim().toUpperCase().replace('-', '_').replace(' ', '_');
                        if (!normalized.isBlank()) {
                    %>
                    <option value="<%= normalized %>" <%= normalized.equals(event.getCategory().name()) ? "selected" : "" %>><%= category %></option>
                    <% }} %>
                <% } else { %>
                    <option value="EDUCATIONAL" <%= event.getCategory() == EventCategory.EDUCATIONAL ? "selected" : "" %>>Educational</option>
                    <option value="SOCIAL" <%= event.getCategory() == EventCategory.SOCIAL ? "selected" : "" %>>Social</option>
                    <option value="SPORTS" <%= event.getCategory() == EventCategory.SPORTS ? "selected" : "" %>>Sports</option>
                    <option value="CULTURAL" <%= event.getCategory() == EventCategory.CULTURAL ? "selected" : "" %>>Cultural</option>
                    <option value="TECHNICAL" <%= event.getCategory() == EventCategory.TECHNICAL ? "selected" : "" %>>Technical</option>
                <% } %>
            </select>
            <button class="btn-solid full" type="submit">Update Event</button>
        </div>

        <div class="panel">
            <h3>Event Image</h3>
            <div id="uploadPreview" class="upload-preview">
                <% if (event.getEventImage() != null && !event.getEventImage().isBlank()) { %>
                <img src="${pageContext.request.contextPath}/media/event-image?name=<%= URLEncoder.encode(event.getEventImage(), "UTF-8") %>" alt="Current image">
                <% } else { %>
                No event image
                <% } %>
            </div>
            <input id="eventImageInput" type="file" name="eventImage" accept="image/*">
            <small>Upload a new image only if needed (max 5MB).</small>
        </div>
    </form>
    <% } %>
</div>

<script>
    const imageInput = document.getElementById("eventImageInput");
    const preview = document.getElementById("uploadPreview");
    if (imageInput && preview) {
        imageInput.addEventListener("change", function () {
            const file = imageInput.files && imageInput.files[0];
            if (!file) return;
            const reader = new FileReader();
            reader.onload = function (e) {
                preview.innerHTML = '<img src="' + e.target.result + '" alt="Preview">';
            };
            reader.readAsDataURL(file);
        });
    }
</script>
</body>
</html>
