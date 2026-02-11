package org.example.vroom.E2E.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class DbUtils {
    private static final String URL = "jdbc:h2:file:./data/vroomdb";
    private static final String USER = "sa";
    private static final String PASS = "";

    public static void insertFinishedRide(Long driverId, Long passengerId, Long routeId) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            conn.setAutoCommit(false);
            try {
                // 1. Clean up existing data for this specific test case
                execute(conn, "DELETE FROM ride_passengers WHERE ride_id IN (SELECT id FROM rides WHERE routes_id = ?)", routeId);
                execute(conn, "DELETE FROM rides WHERE routes_id = ?", routeId);
                execute(conn, "DELETE FROM routes WHERE id = ?", routeId);

                // 2. Insert Route
                String routeSql = "INSERT INTO routes (id, start_location_lat, start_location_lng, end_location_lat, end_location_lng, start_address, end_address) " +
                        "VALUES (?, 45.2396, 19.8227, 45.2491, 19.8550, 'Start Street 1', 'End Avenue 2')";
                try (PreparedStatement pstmt = conn.prepareStatement(routeSql)) {
                    pstmt.setLong(1, routeId);
                    pstmt.executeUpdate();
                }

                // 3. Insert Ride (Empty passengers list is handled by not inserting into the collection table)
                String rideSql = "INSERT INTO rides (driver_id, passenger_id, routes_id, status, is_scheduled, panic_activated, price, start_time, end_time) " +
                        "VALUES (?, ?, ?, 4, false, false, 500.0, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(rideSql)) {
                    pstmt.setLong(1, driverId);
                    pstmt.setLong(2, passengerId);
                    pstmt.setLong(3, routeId);
                    pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now().minusMinutes(15)));
                    pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now().minusMinutes(5)));
                    pstmt.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void execute(Connection conn, String sql, Long param) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, param);
            pstmt.executeUpdate();
        }
    }
}