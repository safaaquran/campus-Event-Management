package com.campus.campus.web.dao;

import com.campus.campus.web.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LookupDAO {
    public List<String> departments() throws SQLException {
        return findAll("departments");
    }

    public List<String> categories() throws SQLException {
        return findAll("categories");
    }

    public void addDepartment(String value) throws SQLException {
        insert("departments", value);
    }

    public void addCategory(String value) throws SQLException {
        insert("categories", value);
    }

    private List<String> findAll(String table) throws SQLException {
        String sql = "SELECT name FROM " + table + " ORDER BY name ASC";
        List<String> values = new ArrayList<>();
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                values.add(rs.getString("name"));
            }
        }
        return values;
    }

    private void insert(String table, String value) throws SQLException {
        String sql = "INSERT INTO " + table + " (name) VALUES (?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value);
            statement.executeUpdate();
        }
    }
}
