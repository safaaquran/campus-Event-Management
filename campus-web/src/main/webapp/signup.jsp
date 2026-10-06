<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sign Up - Campus Events</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
</head>
<body class="auth-page">
<div class="auth-wrap">
    <div class="auth-card page-enter">
        <div class="auth-header">
            <div class="auth-badge"><i class="fa-solid fa-user-plus"></i> Join Campus Events</div>
            <h2>Create Account</h2>
            <p>Register as Admin, Student, or Organizer.</p>
        </div>

        <% String error = request.getParameter("error"); %>
        <% if (error != null) { %>
        <div class="msg err">
            <%= "emailExists".equals(error) ? "Email already exists." : "Please complete all required fields correctly." %>
        </div>
        <% } %>

        <form method="post" action="${pageContext.request.contextPath}/register" class="form-grid">
            <input class="full" type="text" name="fullName" placeholder="Full Name" required>
            <input type="email" name="email" placeholder="Email" required>
            <input type="password" name="password" placeholder="Password" required>

            <select id="facultySelect" name="faculty" required>
                <option value="">Select College</option>
                <option value="Engineering">Engineering</option>
                <option value="Medicine">Medicine</option>
                <option value="Information Technology">Information Technology</option>
                <option value="Agriculture">Agriculture</option>
                <option value="Science and Arts">Science and Arts</option>
                <option value="Medical Sciences">Medical Sciences</option>
                <option value="Pharmacy">Pharmacy</option>
                <option value="Nursing">Nursing</option>
            </select>

            <select id="departmentSelect" name="department" required>
                <option value="">Select Specialization</option>
            </select>

            <select name="admissionYear" required>
                <option value="">Admission Year</option>
                <% for (int y = 2030; y >= 2010; y--) { %>
                <option value="<%= y %>"><%= y %></option>
                <% } %>
            </select>

            <select name="role" required>
                <option value="">Select Role</option>
                <option value="ADMIN">Admin</option>
                <option value="STUDENT">Student</option>
                <option value="ORGANIZER">Organizer</option>
            </select>

            <button class="btn-solid full" type="submit">Sign Up</button>
        </form>

        <div class="auth-footer-links">
            <p>Already have an account? <a href="${pageContext.request.contextPath}/login.jsp">Login</a></p>
            <p><a href="${pageContext.request.contextPath}/index.jsp"><i class="fa-solid fa-arrow-left"></i> Back to Home</a></p>
        </div>
    </div>
</div>

<script>
    const specializationMap = {
        "Medicine": ["Human Medicine", "Dentistry", "Veterinary Medicine", "Nursing"],
        "Pharmacy": ["Pharmacy", "Doctor of Pharmacy"],
        "Engineering": ["Architectural Engineering", "Civil Engineering", "Biomedical Engineering", "Aeronautical Engineering"],
        "Information Technology": ["Cybersecurity & Networks", "Software Engineering", "Computer Engineering", "Artificial Intelligence", "Cybersecurity"],
        "Agriculture": ["Agricultural Engineering", "Plant Production", "Animal Production", "Food Sciences"],
        "Science and Arts": ["Mathematics", "Physics", "Chemistry", "English Language", "Media Studies"],
        "Medical Sciences": ["Clinical Laboratory Sciences", "Radiology", "Public Health", "Respiratory Therapy"],
        "Nursing": ["Adult Nursing", "Pediatric Nursing", "Critical Care Nursing", "Community Health Nursing"]
    };

    const facultySelect = document.getElementById("facultySelect");
    const departmentSelect = document.getElementById("departmentSelect");

    function populateSpecializations() {
        const selectedFaculty = facultySelect.value;
        const specs = specializationMap[selectedFaculty] || [];
        departmentSelect.innerHTML = '<option value="">Select Specialization</option>';
        specs.forEach(function (specialization) {
            const option = document.createElement("option");
            option.value = specialization;
            option.textContent = specialization;
            departmentSelect.appendChild(option);
        });
    }

    facultySelect.addEventListener("change", populateSpecializations);
</script>
</body>
</html>
