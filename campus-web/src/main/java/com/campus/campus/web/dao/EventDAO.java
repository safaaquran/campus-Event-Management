package com.campus.campus.web.dao;

import com.campus.campus.web.model.Event;
import com.campus.campus.web.model.EventCategory;
import com.campus.campus.web.model.EventStatus;
import com.campus.campus.web.model.EventType;
import com.campus.campus.web.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    public void create(Event event) throws SQLException {
        String sql = "INSERT INTO events (title, organizer_id, organizer_name, description, department_club, event_datetime, "
                + "location, capacity, seats_remaining, category, event_type, event_image, status, completed) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, event.getTitle());
            statement.setLong(2, event.getOrganizerId());
            statement.setString(3, event.getOrganizerName());
            statement.setString(4, event.getDescription());
            statement.setString(5, event.getDepartmentClub());
            statement.setTimestamp(6, Timestamp.valueOf(event.getEventDateTime()));
            statement.setString(7, event.getLocation());
            statement.setInt(8, event.getCapacity());
            statement.setInt(9, event.getSeatsRemaining());
            statement.setString(10, event.getCategory().name());
            statement.setString(11, event.getEventType().name());
            statement.setString(12, event.getEventImage());
            statement.setString(13, event.getStatus().name());
            statement.setBoolean(14, event.isCompleted());
            statement.executeUpdate();
        }
    }

    public void update(Event event) throws SQLException {
        String sql = "UPDATE events SET title=?, description=?, department_club=?, event_datetime=?, location=?, capacity=?, "
                + "seats_remaining=?, category=?, event_type=?, event_image=? WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, event.getTitle());
            statement.setString(2, event.getDescription());
            statement.setString(3, event.getDepartmentClub());
            statement.setTimestamp(4, Timestamp.valueOf(event.getEventDateTime()));
            statement.setString(5, event.getLocation());
            statement.setInt(6, event.getCapacity());
            statement.setInt(7, event.getSeatsRemaining());
            statement.setString(8, event.getCategory().name());
            statement.setString(9, event.getEventType().name());
            statement.setString(10, event.getEventImage());
            statement.setLong(11, event.getId());
            statement.executeUpdate();
        }
    }

    public void delete(long eventId) throws SQLException {
        String sql = "DELETE FROM events WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, eventId);
            statement.executeUpdate();
        }
    }

    public Event findById(long eventId) throws SQLException {
        String sql = "SELECT * FROM events WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, eventId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public List<Event> findOpenForRegistration() throws SQLException {
        String sql = "SELECT * FROM events WHERE status = 'OPEN' AND seats_remaining > 0 AND event_datetime >= NOW() ORDER BY event_datetime ASC";
        return queryList(sql, null);
    }

    public List<Event> findByOrganizer(long organizerId) throws SQLException {
        String sql = "SELECT * FROM events WHERE organizer_id = ? ORDER BY event_datetime DESC";
        return queryList(sql, statement -> statement.setLong(1, organizerId));
    }

    public List<Event> findAll() throws SQLException {
        String sql = "SELECT * FROM events ORDER BY event_datetime DESC";
        return queryList(sql, null);
    }

    public List<Event> searchByTitle(String keyword) throws SQLException {
        return searchLike("title", keyword);
    }

    public List<Event> searchByDepartment(String keyword) throws SQLException {
        return searchLike("department_club", keyword);
    }

    public List<Event> searchByCategory(String category) throws SQLException {
        String sql = "SELECT * FROM events WHERE category = ? ORDER BY event_datetime ASC";
        return queryList(sql, statement -> statement.setString(1, category));
    }

    public List<Event> searchByType(String type) throws SQLException {
        String sql = "SELECT * FROM events WHERE event_type = ? ORDER BY event_datetime ASC";
        return queryList(sql, statement -> statement.setString(1, type));
    }

    public List<Event> searchByDate(LocalDate date) throws SQLException {
        String sql = "SELECT * FROM events WHERE DATE(event_datetime) = ? ORDER BY event_datetime ASC";
        return queryList(sql, statement -> statement.setDate(1, java.sql.Date.valueOf(date)));
    }

    public List<Event> searchByAvailability() throws SQLException {
        String sql = "SELECT * FROM events WHERE status='OPEN' AND seats_remaining > 0 AND event_datetime >= NOW() ORDER BY event_datetime ASC";
        return queryList(sql, null);
    }

    public List<String> findDistinctDepartmentsForOpenEvents() throws SQLException {
        String sql = "SELECT DISTINCT department_club FROM events WHERE status = 'OPEN' ORDER BY department_club ASC";
        List<String> departments = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                String value = rs.getString(1);
                if (value != null && !value.isBlank()) {
                    departments.add(value);
                }
            }
        }
        return departments;
    }

    public void setStatus(long eventId, EventStatus status) throws SQLException {
        String sql = "UPDATE events SET status = ? WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.name());
            statement.setLong(2, eventId);
            statement.executeUpdate();
        }
    }

    public void markCompleted(long eventId, boolean completed) throws SQLException {
        String sql = "UPDATE events SET completed = ? WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, completed);
            statement.setLong(2, eventId);
            statement.executeUpdate();
        }
    }

    public void markExpiredEvents() throws SQLException {
        String sql = "UPDATE events SET status='EXPIRED' WHERE event_datetime < NOW() AND status <> 'EXPIRED'";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }

    public int countAttendees(long eventId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservations WHERE event_id = ? AND reservation_status = 'RESERVED'";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, eventId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private List<Event> searchLike(String field, String keyword) throws SQLException {
        String sql = "SELECT * FROM events WHERE " + field + " LIKE ? ORDER BY event_datetime ASC";
        return queryList(sql, statement -> statement.setString(1, "%" + keyword + "%"));
    }

    private List<Event> queryList(String sql, StatementSetter setter) throws SQLException {
        List<Event> events = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (setter != null) {
                setter.apply(statement);
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    events.add(map(rs));
                }
            }
        }
        return events;
    }

    private Event map(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getLong("id"));
        event.setTitle(rs.getString("title"));
        event.setOrganizerId(rs.getLong("organizer_id"));
        event.setOrganizerName(rs.getString("organizer_name"));
        event.setDescription(rs.getString("description"));
        event.setDepartmentClub(rs.getString("department_club"));
        event.setEventDateTime(rs.getTimestamp("event_datetime").toLocalDateTime());
        event.setLocation(rs.getString("location"));
        event.setCapacity(rs.getInt("capacity"));
        event.setSeatsRemaining(rs.getInt("seats_remaining"));
        event.setCategory(EventCategory.valueOf(rs.getString("category")));
        event.setEventType(EventType.valueOf(rs.getString("event_type")));
        event.setEventImage(rs.getString("event_image"));
        event.setStatus(EventStatus.valueOf(rs.getString("status")));
        event.setCompleted(rs.getBoolean("completed"));
        return event;
    }

    @FunctionalInterface
    private interface StatementSetter {
        void apply(PreparedStatement statement) throws SQLException;
    }
}
