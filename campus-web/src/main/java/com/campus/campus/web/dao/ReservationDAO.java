package com.campus.campus.web.dao;

import com.campus.campus.web.model.AttendanceStatus;
import com.campus.campus.web.model.EventStatus;
import com.campus.campus.web.model.Reservation;
import com.campus.campus.web.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    public boolean reserveTicket(long eventId, long studentId) throws SQLException {
        String lockEventSql = "SELECT seats_remaining, status, event_datetime FROM events WHERE id = ? FOR UPDATE";
        String insertReservationSql = "INSERT INTO reservations (event_id, student_id, reservation_status, attendance_status) "
                + "VALUES (?, ?, 'RESERVED', 'UNMARKED')";
        String decrementSeatSql = "UPDATE events SET seats_remaining = seats_remaining - 1 WHERE id = ?";

        try (Connection connection = DBUtil.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement lockStatement = connection.prepareStatement(lockEventSql)) {
                lockStatement.setLong(1, eventId);
                try (ResultSet rs = lockStatement.executeQuery()) {
                    if (!rs.next()) {
                        connection.rollback();
                        return false;
                    }
                    int seats = rs.getInt("seats_remaining");
                    EventStatus status = EventStatus.valueOf(rs.getString("status"));
                    LocalDateTime eventDate = rs.getTimestamp("event_datetime").toLocalDateTime();
                    if (seats <= 0 || status != EventStatus.OPEN || eventDate.isBefore(LocalDateTime.now())) {
                        connection.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement insertStatement = connection.prepareStatement(insertReservationSql);
                 PreparedStatement decrementStatement = connection.prepareStatement(decrementSeatSql)) {
                insertStatement.setLong(1, eventId);
                insertStatement.setLong(2, studentId);
                insertStatement.executeUpdate();

                decrementStatement.setLong(1, eventId);
                decrementStatement.executeUpdate();
                connection.commit();
                return true;
            } catch (SQLException ex) {
                connection.rollback();
                if (ex.getMessage() != null && ex.getMessage().contains("uk_event_student")) {
                    return false;
                }
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public boolean cancelReservation(long eventId, long studentId) throws SQLException {
        String checkSql = "SELECT e.event_datetime FROM reservations r JOIN events e ON r.event_id = e.id "
                + "WHERE r.event_id = ? AND r.student_id = ? FOR UPDATE";
        String deleteSql = "DELETE FROM reservations WHERE event_id = ? AND student_id = ?";
        String incrementSeatSql = "UPDATE events SET seats_remaining = seats_remaining + 1 WHERE id = ?";

        try (Connection connection = DBUtil.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
                checkStatement.setLong(1, eventId);
                checkStatement.setLong(2, studentId);
                try (ResultSet rs = checkStatement.executeQuery()) {
                    if (!rs.next()) {
                        connection.rollback();
                        return false;
                    }
                    Timestamp eventTimestamp = rs.getTimestamp("event_datetime");
                    if (eventTimestamp.toLocalDateTime().isBefore(LocalDateTime.now())) {
                        connection.rollback();
                        return false;
                    }
                }
            }
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql);
                 PreparedStatement incrementStatement = connection.prepareStatement(incrementSeatSql)) {
                deleteStatement.setLong(1, eventId);
                deleteStatement.setLong(2, studentId);
                int deleted = deleteStatement.executeUpdate();
                if (deleted == 0) {
                    connection.rollback();
                    return false;
                }
                incrementStatement.setLong(1, eventId);
                incrementStatement.executeUpdate();
                connection.commit();
                return true;
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public List<Reservation> findByStudent(long studentId) throws SQLException {
        String sql = "SELECT r.*, e.title, e.event_datetime FROM reservations r "
                + "JOIN events e ON e.id = r.event_id WHERE r.student_id = ? ORDER BY e.event_datetime DESC";
        List<Reservation> reservations = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, studentId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    reservations.add(map(rs));
                }
            }
        }
        return reservations;
    }

    public List<Reservation> findPastByStudent(long studentId) throws SQLException {
        String sql = "SELECT r.*, e.title, e.event_datetime FROM reservations r "
                + "JOIN events e ON e.id = r.event_id "
                + "WHERE r.student_id = ? AND r.reservation_status = 'RESERVED' AND e.event_datetime < NOW() "
                + "ORDER BY e.event_datetime DESC";
        List<Reservation> reservations = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, studentId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    reservations.add(map(rs));
                }
            }
        }
        return reservations;
    }

    public List<Reservation> findByEvent(long eventId) throws SQLException {
        String sql = "SELECT r.*, e.title, e.event_datetime, u.full_name AS student_name FROM reservations r "
                + "JOIN events e ON e.id = r.event_id "
                + "JOIN users u ON u.id = r.student_id "
                + "WHERE r.event_id = ? ORDER BY r.reserved_at ASC";
        List<Reservation> reservations = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, eventId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    reservations.add(map(rs));
                }
            }
        }
        return reservations;
    }

    public boolean hasReservation(long eventId, long studentId) throws SQLException {
        String sql = "SELECT 1 FROM reservations WHERE event_id = ? AND student_id = ? AND reservation_status = 'RESERVED'";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, eventId);
            statement.setLong(2, studentId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void markAttendance(long reservationId, AttendanceStatus status) throws SQLException {
        String sql = "UPDATE reservations SET attendance_status = ? WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.name());
            statement.setLong(2, reservationId);
            statement.executeUpdate();
        }
    }

    public boolean canRate(long eventId, long studentId) throws SQLException {
        String sql = "SELECT 1 FROM reservations r JOIN events e ON e.id = r.event_id "
                + "WHERE r.event_id = ? AND r.student_id = ? "
                + "AND r.reservation_status = 'RESERVED' "
                + "AND r.attendance_status = 'PRESENT' "
                + "AND e.event_datetime < NOW()";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, eventId);
            statement.setLong(2, studentId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Reservation map(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getLong("id"));
        reservation.setEventId(rs.getLong("event_id"));
        reservation.setStudentId(rs.getLong("student_id"));
        reservation.setStudentName(getOptionalColumn(rs, "student_name"));
        reservation.setReservationStatus(rs.getString("reservation_status"));
        reservation.setAttendanceStatus(AttendanceStatus.valueOf(rs.getString("attendance_status")));
        reservation.setEventTitle(rs.getString("title"));
        reservation.setEventDateTime(rs.getTimestamp("event_datetime").toLocalDateTime());
        return reservation;
    }

    private String getOptionalColumn(ResultSet rs, String columnName) throws SQLException {
        int columns = rs.getMetaData().getColumnCount();
        for (int i = 1; i <= columns; i++) {
            if (columnName.equalsIgnoreCase(rs.getMetaData().getColumnLabel(i))) {
                return rs.getString(columnName);
            }
        }
        return null;
    }
}
