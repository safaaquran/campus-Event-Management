<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login - Campus Events</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
</head>
<body class="auth-page login-page">
<div class="auth-wrap">
    <div class="auth-card page-enter login-creative-card">
        <div class="login-creative-shell">
            <div class="login-art-panel">
                <div class="login-orb orb-one"></div>
                <div class="login-orb orb-two"></div>
                <h3>Campus Events</h3>
                <p>Plan, discover, and manage campus activities in one place.</p>
                <div class="login-feature-list">
                    <span><i class="fa-solid fa-calendar-check"></i> Smart event tracking</span>
                    <span><i class="fa-solid fa-ticket"></i> Easy reservations</span>
                    <span><i class="fa-solid fa-people-group"></i> Organized community</span>
                </div>
            </div>
            <div class="login-form-panel">
                <div class="auth-header">
                    <h2>Login</h2>
                    <p>Use your email and password to access your account.</p>
                </div>

                <% String error = request.getParameter("error"); %>
                <% String success = request.getParameter("success"); %>
                <% if (error != null) { %>
                <div class="msg err">
                    <%= "blocked".equals(error) ? "Your account is blocked." : "Invalid email or password." %>
                </div>
                <% } %>
                <% if (success != null) { %>
                <div class="msg">Registration completed successfully. Please login.</div>
                <% } %>

                <form method="post" action="${pageContext.request.contextPath}/login">
                    <div class="input-icon-wrap">
                        <i class="fa-regular fa-envelope"></i>
                        <input type="email" name="email" placeholder="Email" required>
                    </div>
                    <div class="input-icon-wrap">
                        <i class="fa-solid fa-lock"></i>
                        <input type="password" name="password" placeholder="Password" required>
                    </div>
                    <button class="btn-solid" type="submit">Login</button>
                </form>

                <div class="auth-footer-links">
                    <p>Don't have an account? <a href="${pageContext.request.contextPath}/signup.jsp">Sign Up</a></p>
                    <p><a href="${pageContext.request.contextPath}/index.jsp"><i class="fa-solid fa-arrow-left"></i> Back to Home</a></p>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
