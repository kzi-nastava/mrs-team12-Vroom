package org.example.vroom.E2E.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.example.vroom.entities.Admin;
import org.example.vroom.entities.Driver;
import org.example.vroom.entities.RegisteredUser;
import org.example.vroom.entities.User;
import org.example.vroom.enums.Gender;

import java.sql.*;
import java.time.LocalDateTime;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

public class DbUtils {
    private static final String URL = "jdbc:h2:file:./data/vroomdb;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASS = "";

    private static Long getUserIdByEmail(Connection conn, String email) throws SQLException {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        }
        return null;
    }

    private static Long executeInsert(Connection conn, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return null;
    }

    public static Long insertVehicle(String brand, String model, String licensePlate) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            String findSql = "SELECT id FROM vehicles WHERE licence_number = ?";
            try (PreparedStatement findStmt = conn.prepareStatement(findSql)) {
                findStmt.setString(1, licensePlate);
                try (ResultSet rs = findStmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getLong("id");
                    }
                }
            }

            String sql = "INSERT INTO vehicles (brand, model, licence_number, type, number_of_seats, babies_allowed, pets_allowed, rating_count, rating_sum) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            return executeInsert(conn, sql,
                    brand,
                    model,
                    licensePlate,
                    "STANDARD",
                    4,
                    true,
                    true,
                    0L,
                    0L
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Long insertDriver(String email, String password, Long vehicleId) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            Long id = getUserIdByEmail(conn, email);
            if(id != null)
                return id;

            String sql = "INSERT INTO users (type, email, password, first_name, last_name, address, phone_number, gender, status, vehicle_id, rating_count, rating_sum, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            return executeInsert(conn, sql,
                    "DRIVER", email, password, "Driver", "Test", "Driver Address", "0987654321", "FEMALE",
                    "AVAILABLE",
                    vehicleId,
                    0L, 0L, Timestamp.valueOf(LocalDateTime.now()));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Long insertUser(String email, String password) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            Long id = getUserIdByEmail(conn, email);
            if(id != null)
                return id;

            String sql = "INSERT INTO users (" +
                    "type, email, password, first_name, last_name, " +
                    "address, phone_number, gender, user_status, created_at" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            return executeInsert(conn, sql,
                    "REGISTERED_USER",
                    email,
                    password,
                    "Test",
                    "User",
                    "Test Address 123",
                    "1234567890",
                    "MALE",
                    "ACTIVE",
                    java.sql.Timestamp.valueOf(java.time.LocalDateTime.now())
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Long insertAdmin(String email, String password) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {Long id = getUserIdByEmail(conn, email);
            if(id != null)
                return id;

            String sql = "INSERT INTO users (" +
                    "type, email, password, first_name, last_name, " +
                    "address, phone_number, gender, created_at" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            return executeInsert(conn, sql,
                    "ADMIN",
                    email,
                    password,
                    "Admin",
                    "Test",
                    "Admin Address 789",
                    "1122334455",
                    "MALE",
                    java.sql.Timestamp.valueOf(java.time.LocalDateTime.now())
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Long insertRoute(Double startLat, Double startLng, Double endLat, Double endLng, String startAddr, String endAddr) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            String sql = "INSERT INTO routes (start_location_lat, start_location_lng, end_location_lat, end_location_lng, start_address, end_address) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            return executeInsert(conn, sql, startLat, startLng, endLat, endLng, startAddr, endAddr);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void insertFinishedRide(Long driverId, Long passengerId, Long routeId) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            conn.setAutoCommit(false);
            try {
                execute(conn, "DELETE FROM ride_passengers WHERE ride_id IN (SELECT id FROM rides WHERE routes_id = ?)", routeId);
                execute(conn, "DELETE FROM rides WHERE routes_id = ?", routeId);
                execute(conn, "DELETE FROM routes WHERE id = ?", routeId);

                String routeSql = "INSERT INTO routes (id, start_location_lat, start_location_lng, end_location_lat, end_location_lng, start_address, end_address) " +
                        "VALUES (?, 45.2396, 19.8227, 45.2491, 19.8550, 'Start Street 1', 'End Avenue 2')";
                try (PreparedStatement pstmt = conn.prepareStatement(routeSql)) {
                    pstmt.setLong(1, routeId);
                    pstmt.executeUpdate();
                }

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