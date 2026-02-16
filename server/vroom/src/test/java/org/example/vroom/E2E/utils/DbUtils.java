package org.example.vroom.E2E.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;
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
    public static TestUserData insertFavoriteRouteScenario(
            Long userId,
            Long driverId,
            Long vehicleId,
            Long routeId,
            Long driverLocationId
    ) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            conn.setAutoCommit(false);

            try {
                String userEmail = "testuser" + System.currentTimeMillis() + "@test.com";
                String driverEmail = "testdriver" + System.currentTimeMillis() + "@test.com";


                String rawPassword = "Zemljaseokrece123";

                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
                String encodedPassword = encoder.encode(rawPassword);
                // CLEANUP

                cleanupTestData(conn, userId, driverId, vehicleId, routeId, driverLocationId);

                String vehicleSql = """
                INSERT INTO vehicles (
                    id, brand, model, type,
                    licence_number, number_of_seats,
                    babies_allowed, pets_allowed,
                    rating_count, rating_sum
                )
                VALUES (?, 'Toyota', 'Corolla', 'STANDARD', 'NS-TEST-001', 4, true, false, 0, 0)
            """;
                executeUpdate(conn, vehicleSql, vehicleId);

                String userSql = """
                INSERT INTO users (
                    id, type, email, password,
                    first_name, last_name, address,
                    gender, phone_number, 
                    user_status, blocked_reason,
                    created_at
                )
                VALUES (?, 'REGISTERED_USER', ?, ?, 'Test', 'User', 'User Address 123',
                        'MALE', '0601234567', 
                        'ACTIVE', NULL,
                        CURRENT_TIMESTAMP)
            """;
                try (PreparedStatement pstmt = conn.prepareStatement(userSql)) {
                    pstmt.setLong(1, userId);
                    pstmt.setString(2, userEmail);
                    pstmt.setString(3, encodedPassword);
                    pstmt.executeUpdate();
                }

                String driverSql = """
                INSERT INTO users (
                    id, type, email, password,
                    first_name, last_name, address,
                    gender, phone_number,
                    status, rating_count, rating_sum,
                    vehicle_id, user_status, blocked_reason,
                    created_at
                )
                VALUES (?, 'DRIVER', ?, ?, 'Test', 'Driver', 'Driver Address 456',
                        'MALE', '0607654321', 
                        'AVAILABLE', 0, 0, 
                        ?, 'ACTIVE', NULL,
                        CURRENT_TIMESTAMP)
            """;
                try (PreparedStatement pstmt = conn.prepareStatement(driverSql)) {
                    pstmt.setLong(1, driverId);
                    pstmt.setString(2, driverEmail);
                    pstmt.setString(3, encodedPassword);
                    pstmt.setLong(4, vehicleId);
                    pstmt.executeUpdate();
                }
                String locationSql = """
                INSERT INTO driver_locations (
                    id, driver_id, latitude, longitude, last_updated
                )
                VALUES (?, ?, 45.2396, 19.8227, CURRENT_TIMESTAMP)
            """;
                try (PreparedStatement pstmt = conn.prepareStatement(locationSql)) {
                    pstmt.setLong(1, driverLocationId);
                    pstmt.setLong(2, driverId);
                    pstmt.executeUpdate();
                }

                String routeSql = """
                INSERT INTO routes (
                    id,
                    start_location_lat, start_location_lng,
                    end_location_lat, end_location_lng,
                    start_address, end_address
                )
                VALUES (?, 45.2396, 19.8227, 45.2491, 19.8550,
                        'Bulevar oslobođenja 46, Novi Sad',
                        'Trg slobode 1, Novi Sad')
            """;
                executeUpdate(conn, routeSql, routeId);

                String favoriteSql = """
                INSERT INTO favorite_routes (
                    user_id, route_id,
                    name, start_address, end_address
                )
                VALUES (?, ?, 'Home to Downtown',
                        'Bulevar oslobođenja 46, Novi Sad',
                        'Trg slobode 1, Novi Sad')
            """;
                try (PreparedStatement pstmt = conn.prepareStatement(favoriteSql)) {
                    pstmt.setLong(1, userId);
                    pstmt.setLong(2, routeId);
                    pstmt.executeUpdate();
                }

                conn.commit();
                return new TestUserData(userEmail, rawPassword);

            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Failed to setup test data", e);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed", e);
        }
    }

    private static void cleanupTestData(
            Connection conn,
            Long userId,
            Long driverId,
            Long vehicleId,
            Long routeId,
            Long driverLocationId
    ) throws SQLException {

        execute(conn, "DELETE FROM favorite_routes WHERE user_id = ?", userId);
        execute(conn, "DELETE FROM favorite_routes WHERE route_id = ?", routeId);

        conn.prepareStatement(
                "DELETE FROM ride_passengers WHERE ride_id IN " +
                        "(SELECT id FROM rides WHERE driver_id = " + driverId + " OR passenger_id = " + userId + ")"
        ).executeUpdate();

        conn.prepareStatement(
                "DELETE FROM rides WHERE driver_id = " + driverId + " OR passenger_id = " + userId
        ).executeUpdate();
        execute(conn, "DELETE FROM rides WHERE routes_id = ?", routeId);

        execute(conn, "DELETE FROM driver_locations WHERE driver_id = ?", driverId);
        execute(conn, "DELETE FROM driver_locations WHERE id = ?", driverLocationId);
        execute(conn, "UPDATE users SET vehicle_id = NULL WHERE id = ?", driverId);
        execute(conn, "DELETE FROM users WHERE id = ?", driverId);
        execute(conn, "DELETE FROM users WHERE id = ?", userId);
        execute(conn, "DELETE FROM routes WHERE id = ?", routeId);
        execute(conn, "DELETE FROM vehicles WHERE id = ?", vehicleId);
    }




    private static void executeUpdate(Connection conn, String sql, Long param) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, param);
            pstmt.executeUpdate();
        }
    }
}