package org.example.vroom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.example.vroom.entities.Driver;
import org.example.vroom.entities.RegisteredUser;
import org.example.vroom.entities.Ride;
import org.example.vroom.entities.Route;
import org.example.vroom.enums.DriverStatus;
import org.example.vroom.enums.Gender;
import org.example.vroom.enums.RideStatus;
import org.example.vroom.repositories.DriverRepository;
import org.example.vroom.repositories.RegisteredUserRepository;
import org.example.vroom.repositories.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FinishRideControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private RegisteredUserRepository registeredUserRepository;

    private Driver driver;
    private RegisteredUser passenger;
    private Ride ongoingRide;
    private Long ongoingRideId;

    private Driver createDriver() {
        return Driver.builder()
                .firstName("John")
                .lastName("Driver")
                .email("driver@test.com")
                .password("password123")
                .address("Bulevar Oslobodjenja 1")
                .phoneNumber("0641234567")
                .gender(Gender.MALE)
                .status(DriverStatus.UNAVAILABLE)
                .build();
    }

    private Route createRoute() {
        Route route = new Route();
        route.setStartLocationLat(45.2551);
        route.setStartLocationLng(19.8451);
        route.setEndLocationLat(45.2551);
        route.setEndLocationLng(19.8451);
        route.setStartAddress("Trg Slobode 1, Novi Sad");
        route.setStops(new ArrayList<>());
        return route;
    }

    private void setupTestData() {
        driver = createDriver();
        driver = driverRepository.save(driver);

        passenger = RegisteredUser.builder()
                .firstName("Jane")
                .lastName("Passenger")
                .email("passenger@test.com")
                .password("password456")
                .address("Mileve Maric 26")
                .phoneNumber("0641234567")
                .gender(Gender.FEMALE)
                .build();
        passenger = registeredUserRepository.save(passenger);

        ongoingRide = Ride.builder()
                .driver(driver)
                .passenger(passenger)
                .route(createRoute())
                .status(RideStatus.ONGOING)
                .isScheduled(false)
                .startTime(LocalDateTime.now().minusMinutes(15))
                .price(50.0)
                .passengers(List.of("extra1@example.com", "extra2@example.com"))
                .build();

        ongoingRide = rideRepository.save(ongoingRide);
        ongoingRideId = ongoingRide.getId();
    }

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        setupTestData();
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("POST /api/rides/{rideId}/finish - Success")
    void finishRide_integrationSuccess() throws Exception {
        mockMvc.perform(post("/api/rides/{rideId}/finish", ongoingRideId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Success")));

        Ride updatedRide = rideRepository.findById(ongoingRideId).orElseThrow();
        assert updatedRide.getStatus() == RideStatus.FINISHED;
        assert updatedRide.getEndTime() != null;

        Driver updatedDriver = driverRepository.findById(driver.getId()).orElseThrow();
        assert updatedDriver.getStatus() == DriverStatus.AVAILABLE;
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("POST /api/rides/{rideId}/finish - Ride not found")
    void finishRide_rideNotFound() throws Exception {
        mockMvc.perform(post("/api/rides/{rideId}/finish", -1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Ride not found")));
    }

    @Test
    @WithMockUser(roles = "PASSENGER")
    @DisplayName("POST /api/rides/{rideId}/finish - Forbidden Role")
    void finishRide_forbidden_passenger() throws Exception {
        mockMvc.perform(post("/api/rides/{rideId}/finish", ongoingRideId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/rides/{rideId}/finish - Unauthorized")
    void finishRide_unauthorized() throws Exception {
        mockMvc.perform(post("/api/rides/{rideId}/finish", ongoingRideId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"DRIVER", "ADMIN"})
    @DisplayName("POST /api/rides/{rideId}/finish - Multiple Roles")
    void finishRide_multipleRolesSuccess() throws Exception {
        mockMvc.perform(post("/api/rides/{rideId}/finish", ongoingRideId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}