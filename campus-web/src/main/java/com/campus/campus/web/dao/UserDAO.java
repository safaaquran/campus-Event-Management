package com.campus.campus.web.dao;

import com.campus.campus.web.model.Role;
import com.campus.campus.web.model.User;
import com.campus.campus.web.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public void create(User user) throws SQLException {
        String sql = "INSERT INTO users (full_name, email, password_hash, faculty, department, admission_year, role, blocked) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getFullName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getFaculty());
            statement.setString(5, user.getDepartment());
            statement.setInt(6, user.getAdmissionYear());
            statement.setString(7, user.getRole().name());
            statement.setBoolean(8, user.isBlocked());
            statement.executeUpdate();
        }
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public User findById(long id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public void updateProfile(User user) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, email = ?, faculty = ?, department = ?, admission_year = ?, password_hash = ? WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getFullName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getFaculty());
            statement.setString(4, user.getDepartment());
            statement.setInt(5, user.getAdmissionYear());
            statement.setString(6, user.getPasswordHash());
            statement.setLong(7, user.getId());
            statement.executeUpdate();
        }
    }

    public boolean emailExistsForAnotherUser(String email, long userId) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ? AND id <> ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setLong(2, userId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        List<User> users = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                users.add(map(rs));
            }
        }
        return users;
    }

    public void blockOrUnblock(long userId, boolean blocked) throws SQLException {
        String sql = "UPDATE users SET blocked = ? WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, blocked);
            statement.setLong(2, userId);
            statement.executeUpdate();
        }
    }

    public void deleteUser(long userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.executeUpdate();
        }
    }

    public void updateByAdmin(User user) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, email = ?, role = ?, faculty = ?, department = ?, admission_year = ?, blocked = ? "
                + "WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getFullName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getRole().name());
            statement.setString(4, user.getFaculty());
            statement.setString(5, user.getDepartment());
            statement.setInt(6, user.getAdmissionYear());
            statement.setBoolean(7, user.isBlocked());
            statement.setLong(8, user.getId());
            statement.executeUpdate();
        }
    }

    private User map(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFaculty(rs.getString("faculty"));
        user.setDepartment(rs.getString("department"));
        user.setAdmissionYear(rs.getInt("admission_year"));
        user.setRole(Role.valueOf(rs.getString("role")));
        user.setBlocked(rs.getBoolean("blocked"));
        return user;
    }
}
