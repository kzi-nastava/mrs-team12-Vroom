package org.example.vroom.E2E.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.example.vroom.entities.Admin;
import org.example.vroom.entities.Driver;
import org.example.vroom.entities.RegisteredUser;
import org.example.vroom.entities.User;
import org.example.vroom.enums.Gender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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