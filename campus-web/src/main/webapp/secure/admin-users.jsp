<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List,com.campus.campus.web.model.User" %>
<%
    List<User> users = (List<User>) request.getAttribute("users");
%>
<!DOCTYPE html>
<html>
<head>
    <title>User Dashboard</title>
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
    <h2>User Dashboard</h2>
    <div class="landing-actions action-row" style="justify-content:flex-start;">
        <a class="btn btn-view" href="${pageContext.request.contextPath}/secure/admin/events"><i class="fa-regular fa-calendar"></i> Open Event Dashboard</a>
    </div>
    <div class="toolbar-grid">
        <div class="search-wrap">
            <i class="fa-solid fa-magnifying-glass"></i>
            <input id="adminUserSearch" type="text" placeholder="Search by name or email">
        </div>
        <select id="adminRoleFilter">
            <option value="">All Roles</option>
            <option value="STUDENT">Student</option>
            <option value="ORGANIZER">Organizer</option>
            <option value="ADMIN">Admin</option>
        </select>
        <button class="btn-solid" type="button" onclick="toggleAddUser()">+ Add User</button>
    </div>

    <div id="addUserPanel" class="panel" style="display:none;margin-bottom:16px;">
        <h3>Create New User</h3>
        <form method="post" action="${pageContext.request.contextPath}/secure/admin/users" class="form-grid">
            <input type="hidden" name="action" value="create">
            <input name="fullName" placeholder="Full Name" required>
            <input type="email" name="email" placeholder="Email" required>
            <input type="password" name="password" placeholder="Password" required>
            <select name="role" required>
                <option value="STUDENT">Student</option>
                <option value="ORGANIZER">Organizer</option>
                <option value="ADMIN">Admin</option>
            </select>
            <input name="faculty" placeholder="Faculty" required>
            <input name="department" placeholder="Department" required>
            <input type="number" name="admissionYear" placeholder="Admission Year" required>
            <button class="btn-solid full" type="submit">Create User</button>
        </form>
    </div>

    <div id="adminUserCards" class="admin-user-grid">
        <% for (User user : users) { %>
        <div class="admin-user-card" data-name="<%= user.getFullName().toLowerCase() %>" data-email="<%= user.getEmail().toLowerCase() %>" data-role="<%= user.getRole().name() %>">
            <div class="admin-user-header">
                <h3><i class="fa-regular fa-circle-user"></i> <%= user.getFullName() %></h3>
                <span class="status-badge <%= user.isBlocked() ? "badge-full" : "badge-active" %>"><%= user.isBlocked() ? "Blocked" : "Active" %></span>
            </div>
            <p><strong>Email:</strong> <%= user.getEmail() %></p>
            <p><strong>Role:</strong> <%= user.getRole() %></p>
            <p><strong>Faculty:</strong> <%= user.getFaculty() %></p>
            <p><strong>Department:</strong> <%= user.getDepartment() %></p>

            <details class="admin-user-details">
                <summary>Edit / Change Role</summary>
                <form method="post" action="${pageContext.request.contextPath}/secure/admin/users" class="form-grid">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="userId" value="<%= user.getId() %>">
                    <input name="fullName" value="<%= user.getFullName() %>" required>
                    <input name="email" value="<%= user.getEmail() %>" required>
                    <select name="role" required>
                        <option value="STUDENT" <%= "STUDENT".equals(user.getRole().name()) ? "selected" : "" %>>Student</option>
                        <option value="ORGANIZER" <%= "ORGANIZER".equals(user.getRole().name()) ? "selected" : "" %>>Organizer</option>
                        <option value="ADMIN" <%= "ADMIN".equals(user.getRole().name()) ? "selected" : "" %>>Admin</option>
                    </select>
                    <input name="faculty" value="<%= user.getFaculty() %>" required>
                    <input name="department" value="<%= user.getDepartment() %>" required>
                    <input type="number" name="admissionYear" value="<%= user.getAdmissionYear() %>" required>
                    <button class="btn-solid full" type="submit">Save Changes</button>
                </form>
            </details>

            <div class="card-actions">
                <form class="inline-form" method="post" action="${pageContext.request.contextPath}/secure/admin/users">
                    <input type="hidden" name="userId" value="<%= user.getId() %>">
                    <% if (user.isBlocked()) { %>
                    <input type="hidden" name="action" value="unblock">
                    <button class="btn btn-sm btn-edit" type="submit">Unblock</button>
                    <% } else { %>
                    <input type="hidden" name="action" value="block">
                    <button class="warn btn-sm" type="submit">Block</button>
                    <% } %>
                </form>
                <form class="inline-form" method="post" action="${pageContext.request.contextPath}/secure/admin/users" onsubmit="return confirm('Delete this user?');">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="userId" value="<%= user.getId() %>">
                    <button class="danger btn-sm" type="submit">Delete</button>
                </form>
            </div>
        </div>
        <% } %>
    </div>
</div>

<script>
    function toggleAddUser() {
        const panel = document.getElementById('addUserPanel');
        panel.style.display = panel.style.display === 'none' ? 'block' : 'none';
    }

    const searchInput = document.getElementById('adminUserSearch');
    const roleFilter = document.getElementById('adminRoleFilter');
    const cards = Array.from(document.querySelectorAll('.admin-user-card'));

    function applyAdminFilters() {
        const q = (searchInput.value || '').trim().toLowerCase();
        const role = roleFilter.value;
        cards.forEach(function (card) {
            const name = card.dataset.name || '';
            const email = card.dataset.email || '';
            const cardRole = card.dataset.role || '';
            const matchSearch = !q || name.includes(q) || email.includes(q);
            const matchRole = !role || cardRole === role;
            card.style.display = (matchSearch && matchRole) ? '' : 'none';
        });
    }

    searchInput.addEventListener('input', applyAdminFilters);
    roleFilter.addEventListener('change', applyAdminFilters);
</script>
</body>
</html>
