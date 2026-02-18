package org.example.vroom.repository;


import jakarta.transaction.Transactional;
import org.example.vroom.entities.*;
import org.example.vroom.enums.DriverStatus;
import org.example.vroom.enums.Gender;
import org.example.vroom.enums.VehicleType;
import org.example.vroom.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DriverRepositoryTest {
    private int vehicleCounter = 0;
    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DriverLocationRepository driverLocationRepository;

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private DriverProfileUpdateRequestRepository driverRequestRepository;

    private Driver driver1;
    private Driver driver2;
    private Driver driver3;

    private static final Double NOVI_SAD_LAT = 45.2551;
    private static final Double NOVI_SAD_LNG = 19.8451;
    private static final Double MEDIUM_DISTANCE_LAT = 45.2600;
    private static final Double MEDIUM_DISTANCE_LNG = 19.8500;
    private static final Double FAR_DISTANCE_LAT = 45.3000;
    private static final Double FAR_DISTANCE_LNG = 19.9000;

    @BeforeEach
    void setUp() {
        vehicleCounter = 0;
    }

    @Test
    @DisplayName("findFirstAvailableDriver - finds closest available driver")
    void findFirstAvailableDriver_findsClosestDriver() {
        // Arrange
        driver1 = createDriverWithLocation(
                "Marko", "Markovic", "STANDARD",
                NOVI_SAD_LAT, NOVI_SAD_LNG, DriverStatus.AVAILABLE
        );

        driver2 = createDriverWithLocation(
                "Petar", "Petrovic", "STANDARD",
                MEDIUM_DISTANCE_LAT, MEDIUM_DISTANCE_LNG, DriverStatus.AVAILABLE
        );

        driver3 = createDriverWithLocation(
                "Jovan", "Jovanovic", "STANDARD",
                FAR_DISTANCE_LAT, FAR_DISTANCE_LNG, DriverStatus.AVAILABLE
        );

        // Act
        Optional<Driver> result = driverRepository.findFirstAvailableDriver(
                VehicleType.STANDARD,
                Boolean.FALSE,
                Boolean.FALSE,
                NOVI_SAD_LAT,
                NOVI_SAD_LNG
        );

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Marko", result.get().getFirstName());
    }

    @Test
    @DisplayName("findFirstAvailableDriver - no available drivers - returns empty")
    void findFirstAvailableDriver_noAvailableDrivers_returnsEmpty() {
        // Arrange - All drivers UNAVAILABLE
        driver1 = createDriverWithLocation(
                "Marko", "Markovic", "STANDARD",
                NOVI_SAD_LAT, NOVI_SAD_LNG, DriverStatus.UNAVAILABLE
        );

        // Act
        Optional<Driver> result = driverRepository.findFirstAvailableDriver(
                VehicleType.STANDARD,
                Boolean.FALSE,
                Boolean.FALSE,
                NOVI_SAD_LAT,
                NOVI_SAD_LNG
        );

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findFirstAvailableDriver - filters by vehicle type")
    void findFirstAvailableDriver_filtersByVehicleType() {
        // Arrange
        driver1 = createDriverWithLocation(
                "Marko", "Markovic", "STANDARD",
                NOVI_SAD_LAT, NOVI_SAD_LNG, DriverStatus.AVAILABLE
        );

        driver2 = createDriverWithLocation(
                "Petar", "Petrovic", "LUXURY",
                MEDIUM_DISTANCE_LAT, MEDIUM_DISTANCE_LNG, DriverStatus.AVAILABLE
        );

        // Act
        Optional<Driver> result = driverRepository.findFirstAvailableDriver(
                VehicleType.LUXURY,
                Boolean.FALSE,
                Boolean.FALSE,
                NOVI_SAD_LAT,
                NOVI_SAD_LNG
        );

        // Assert
        assertTrue(result.isPresent());
        assertEquals(VehicleType.LUXURY, result.get().getVehicle().getType());
        assertEquals("Petar", result.get().getFirstName());
    }

    @Test
    @DisplayName("findFirstAvailableDriver - filters by babies allowed")
    void findFirstAvailableDriver_filtersByBabiesAllowed() {
        // Arrange
        Vehicle vehicle1 = createVehicle(VehicleType.STANDARD, false, false);
        driver1 = createDriver("Marko", "Markovic", vehicle1, DriverStatus.AVAILABLE);
        createDriverLocation(driver1, NOVI_SAD_LAT, NOVI_SAD_LNG);


        Vehicle vehicle2 = createVehicle(VehicleType.STANDARD, true, false);
        driver2 = createDriver("Petar", "Petrovic", vehicle2, DriverStatus.AVAILABLE);
        createDriverLocation(driver2, MEDIUM_DISTANCE_LAT, MEDIUM_DISTANCE_LNG);

        // Act
        Optional<Driver> result = driverRepository.findFirstAvailableDriver(
                VehicleType.STANDARD,
                Boolean.TRUE,
                Boolean.FALSE,
                NOVI_SAD_LAT,
                NOVI_SAD_LNG
        );

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().getVehicle().getBabiesAllowed());
        assertEquals("Petar", result.get().getFirstName());
    }

    @Test
    @DisplayName("findFirstAvailableDriver - filters by pets allowed")
    void findFirstAvailableDriver_filtersByPetsAllowed() {
        Vehicle vehicle1 = createVehicle(VehicleType.STANDARD, false, false);
        driver1 = createDriver("Marko", "Markovic", vehicle1, DriverStatus.AVAILABLE);
        createDriverLocation(driver1, NOVI_SAD_LAT, NOVI_SAD_LNG);


        Vehicle vehicle2 = createVehicle(VehicleType.STANDARD, false, true);
        driver2 = createDriver("Petar", "Petrovic", vehicle2, DriverStatus.AVAILABLE);
        createDriverLocation(driver2, MEDIUM_DISTANCE_LAT, MEDIUM_DISTANCE_LNG);

        // Act
        Optional<Driver> result = driverRepository.findFirstAvailableDriver(
                VehicleType.STANDARD,
                Boolean.FALSE,
                Boolean.TRUE,
                NOVI_SAD_LAT,
                NOVI_SAD_LNG
        );

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().getVehicle().getPetsAllowed());
        assertEquals("Petar", result.get().getFirstName());
    }

    @Test
    @DisplayName("findFirstAvailableDriver - null babies/pets params - ignores filter")
    void findFirstAvailableDriver_nullParams_ignoresFilter() {
        // Arrange
        driver1 = createDriverWithLocation(
                "Marko", "Markovic", "STANDARD",
                NOVI_SAD_LAT, NOVI_SAD_LNG, DriverStatus.AVAILABLE
        );

        // Act
        Optional<Driver> result = driverRepository.findFirstAvailableDriver(
                VehicleType.STANDARD,
                null,
                null,
                NOVI_SAD_LAT,
                NOVI_SAD_LNG
        );

        // Assert
        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("findFirstAvailableDriver - multiple criteria match - returns closest")
    void findFirstAvailableDriver_multipleCriteriaMatch_returnsClosest() {
        // Arrange
        Vehicle vehicle1 = createVehicle(VehicleType.LUXURY, true, true);
        driver1 = createDriver("Marko", "Markovic", vehicle1, DriverStatus.AVAILABLE);
        createDriverLocation(driver1, NOVI_SAD_LAT, NOVI_SAD_LNG); // CLOSEST

        Vehicle vehicle2 = createVehicle(VehicleType.LUXURY, true, true);
        driver2 = createDriver("Petar", "Petrovic", vehicle2, DriverStatus.AVAILABLE);
        createDriverLocation(driver2, MEDIUM_DISTANCE_LAT, MEDIUM_DISTANCE_LNG);

        // Act
        Optional<Driver> result = driverRepository.findFirstAvailableDriver(
                VehicleType.LUXURY,
                Boolean.TRUE,
                Boolean.TRUE,
                NOVI_SAD_LAT,
                NOVI_SAD_LNG
        );

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Marko", result.get().getFirstName());
    }

    @Test
    @DisplayName("findFirstAvailableDriver - driver without location - not found")
    void findFirstAvailableDriver_driverWithoutLocation_notFound() {
        // Arrange
        Vehicle vehicle = createVehicle(VehicleType.STANDARD, false, false);
        driver1 = createDriver("Marko", "Markovic", vehicle, DriverStatus.AVAILABLE);


        // Act
        Optional<Driver> result = driverRepository.findFirstAvailableDriver(
                VehicleType.STANDARD,
                Boolean.FALSE,
                Boolean.FALSE,
                NOVI_SAD_LAT,
                NOVI_SAD_LNG
        );

        // Assert
        assertFalse(result.isPresent());
    }

    // helper methods

    private Driver createDriverWithLocation(
            String firstName,
            String lastName,
            String vehicleType,
            Double latitude,
            Double longitude,
            DriverStatus status
    ) {
        VehicleType type = VehicleType.valueOf(vehicleType);
        Vehicle vehicle = createVehicle(type, false, false);
        Driver driver = createDriver(firstName, lastName, vehicle, status);
        createDriverLocation(driver, latitude, longitude);
        return driver;
    }

    private Driver createDriver(String firstName, String lastName, Vehicle vehicle, DriverStatus status) {
        Driver driver = Driver.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(firstName.toLowerCase() + "@test.com")
                .password("password123")
                .address("Test Address")
                .phoneNumber("0601234567")
                .gender(Gender.MALE)
                .vehicle(vehicle)
                .status(status)
                .build();
        return driverRepository.save(driver);
    }

    private Vehicle createVehicle(VehicleType type, boolean babiesAllowed, boolean petsAllowed) {
        String uniquePlate = "NS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + "-AB";

        Vehicle vehicle = Vehicle.builder()
                .type(type)
                .brand("Test Brand")
                .model("Test Model")
                .licenceNumber(uniquePlate)
                .numberOfSeats(4)
                .babiesAllowed(babiesAllowed)
                .petsAllowed(petsAllowed)
                .build();
        return vehicleRepository.save(vehicle);
    }

    private void createDriverLocation(Driver driver, Double latitude, Double longitude) {
        DriverLocation location = DriverLocation.builder()
                .driver(driver)
                .latitude(latitude)
                .longitude(longitude)
                .lastUpdated(LocalDateTime.now())
                .build();
        driverLocationRepository.save(location);
    }
}
