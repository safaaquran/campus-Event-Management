<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.campus.campus.web.model.User,java.util.List" %>
<%
    User organizer = (User) session.getAttribute("user");
    List<String> departments = (List<String>) request.getAttribute("departments");
    List<String> categories = (List<String>) request.getAttribute("categories");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Create Event</title>
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
        <h2>Create Event</h2>
        <p>Fill all required details to publish a new campus event.</p>
    </div>

    <form method="post" action="${pageContext.request.contextPath}/secure/organizer/create-event" enctype="multipart/form-data" class="split-layout">
        <div class="panel form-grid">
            <input class="full" name="title" placeholder="Event Title" required>
            <input name="organizerName" value="<%= organizer == null ? "" : organizer.getFullName() %>" placeholder="Organizer Name" required>
            <select name="departmentClub" required>
                <option value="">Department / Club</option>
                <% if (departments != null && !departments.isEmpty()) { %>
                    <% for (String department : departments) { %>
                    <option value="<%= department %>"><%= department %></option>
                    <% } %>
                <% } else { %>
                    <option value="Engineering Club">Engineering Club</option>
                    <option value="Medical Students Club">Medical Students Club</option>
                    <option value="IT Innovation Club">IT Innovation Club</option>
                    <option value="Agriculture Club">Agriculture Club</option>
                    <option value="Science Society">Science Society</option>
                    <option value="Arts and Culture Club">Arts and Culture Club</option>
                    <option value="Pharmacy Club">Pharmacy Club</option>
                    <option value="Nursing Club">Nursing Club</option>
                <% } %>
            </select>

            <textarea class="full" name="description" placeholder="Description" required></textarea>
            <input type="datetime-local" name="eventDateTime" required>
            <select name="location" required>
                <option value="">Location</option>
                <option value="NG">NG</option>
                <option value="NB">NB</option>
                <option value="NF">NF</option>
                <option value="SA">SA</option>
                <option value="N2">N2</option>
                <option value="IBN SINA HALL">IBN SINA HALL</option>
                <option value="SALAH ALDEEN HALL">SALAH ALDEEN HALL</option>
                <option value="LIBRARY">LIBRARY</option>
                <option value="M4">M4</option>
            </select>

            <input type="number" min="1" name="capacity" placeholder="Capacity (max attendees)" required>
            <select name="category" required>
                <option value="">Category</option>
                <% if (categories != null && !categories.isEmpty()) { %>
                    <% for (String category : categories) {
                        String normalized = category == null ? "" : category.trim().toUpperCase().replace('-', '_').replace(' ', '_');
                        if (!normalized.isBlank()) {
                    %>
                    <option value="<%= normalized %>"><%= category %></option>
                    <% }} %>
                <% } else { %>
                    <option value="EDUCATIONAL">Educational</option>
                    <option value="SOCIAL">Social</option>
                    <option value="SPORTS">Sports</option>
                    <option value="CULTURAL">Cultural</option>
                    <option value="TECHNICAL">Technical</option>
                <% } %>
            </select>

            <select name="eventType" required>
                <option value="">Event Type</option>
                <option value="WORKSHOP">Workshop</option>
                <option value="SEMINAR">Seminar</option>
                <option value="CLUB_SOCIAL_EVENT">Club Social Event</option>
                <option value="SPORTS_ACTIVITY">Sports Activity</option>
            </select>
            <button class="btn-solid full" type="submit">Create Event</button>
        </div>

        <div class="panel">
            <h3><i class="fa-regular fa-image"></i> Event Image</h3>
            <div id="uploadPreview" class="upload-preview">Image preview will appear here</div>
            <input id="eventImageInput" type="file" name="eventImage" accept="image/*">
            <small>Maximum image size: 5MB.</small>
        </div>
    </form>
</div>

<script>
    const imageInput = document.getElementById("eventImageInput");
    const preview = document.getElementById("uploadPreview");

    imageInput.addEventListener("change", function () {
        const file = imageInput.files && imageInput.files[0];
        if (!file) {
            preview.innerHTML = "Image preview will appear here";
            return;
        }
        const reader = new FileReader();
        reader.onload = function (e) {
            preview.innerHTML = '<img src="' + e.target.result + '" alt="Preview">';
        };
        reader.readAsDataURL(file);
    });
</script>
</body>
</html>
