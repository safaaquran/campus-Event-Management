package com.campus.campus.web.dao;

import com.campus.campus.web.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RatingDAO {
    public void upsertRating(long eventId, long studentId, int rating, String feedback) throws SQLException {
        String sql = "INSERT INTO event_ratings (event_id, student_id, rating, feedback) VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE rating = VALUES(rating), feedback = VALUES(feedback)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, eventId);
            statement.setLong(2, studentId);
            statement.setInt(3, rating);
            statement.setString(4, feedback);
            statement.executeUpdate();
        }
    }

    public double averageForEvent(long eventId) throws SQLException {
        String sql = "SELECT AVG(rating) FROM event_ratings WHERE event_id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, eventId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }
}
