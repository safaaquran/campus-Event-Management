package com.campus.campus.web.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBUtil {
    private static final String URL = System.getenv().getOrDefault(
            "CAMPUS_DB_URL",
            "jdbc:mysql://localhost:3306/campus_event_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
    );
    private static final String USER = System.getenv().getOrDefault("CAMPUS_DB_USER", "root");
    private static final String PASS = System.getenv().getOrDefault("CAMPUS_DB_PASS", "rand123");

    private DBUtil() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
