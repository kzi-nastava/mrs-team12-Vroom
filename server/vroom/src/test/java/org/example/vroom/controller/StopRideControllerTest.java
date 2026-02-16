package org.example.vroom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.example.vroom.DTOs.requests.ride.StopRideRequestDTO;
import org.example.vroom.entities.*;
import org.example.vroom.enums.DriverStatus;
import org.example.vroom.enums.Gender;
import org.example.vroom.enums.RideStatus;
import org.example.vroom.enums.VehicleType;
import org.example.vroom.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class StopRideControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private RegisteredUserRepository registeredUserRepository;
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private PriceListRepository pricelistRepository;

    private Driver driver;
    private RegisteredUser passenger;
    private Ride ongoingRide;
    private Long ongoingRideId;
    private Pricelist createPricelist() {
        return Pricelist.builder()
                .valid(true)
                .priceStandard(100.0)
                .priceLuxury(200.0)
                .priceMinivan(150.0)
                .build();
    }

    private Driver createDriver(){
        Vehicle vehicle = Vehicle.builder()
                .brand("Toyota")
                .model("Corolla")
                .type(VehicleType.STANDARD)
                .licenceNumber("NS123AB")
                .numberOfSeats(4)
                .babiesAllowed(false)
                .petsAllowed(false)
                .build();
        return Driver.builder()
                .firstName("Marko")
                .lastName("Markovic")
                .email("marko.driver@test.com")
                .password("password123")
                .address("Bulevar Oslobodjenja 1")
                .phoneNumber("0601234567")
                .gender(Gender.MALE)
                .status(DriverStatus.UNAVAILABLE)
                .ratingCount(0L)
                .ratingSum(0L)
                .vehicle(vehicle)
                .build();
    }


    private Route createRoute(){
        Route route = new Route();
        route.setStartLocationLat(45.2551);
        route.setStartLocationLng(19.8451);
        route.setStartAddress("Trg Slobode 1, Novi Sad");
        route.setEndLocationLat(0.0);
        route.setEndLocationLng(0.0);
        route.setEndAddress(null);
        route.setStops(new ArrayList<>());

        return route;
    }

    private Ride createRide(Route route){
        return Ride.builder()
                .driver(driver)
                .passenger(passenger)
                .route(route)
                .status(RideStatus.ONGOING)
                .isScheduled(false)
                .startTime(LocalDateTime.now().minusMinutes(15))
                .price(0.0)
                .build();
    }

    private void setupTestData() {

        Pricelist pricelist = createPricelist();
        pricelistRepository.save(pricelist);
        driver = createDriver();
        driver = driverRepository.save(driver);


        passenger = RegisteredUser.builder()
                .firstName("Jovan")
                .lastName("Jovic")
                .email("jovan.passenger@test.com")
                .password("password456")
                .address("Ulica Svetog Save 2")
                .phoneNumber("0619876543")
                .gender(Gender.MALE)
                .build();
        passenger = registeredUserRepository.save(passenger);

        Route route = createRoute();

        ongoingRide = createRide(route);

        ongoingRide = rideRepository.save(ongoingRide);
        ongoingRideId = ongoingRide.getId();
    }

    @BeforeEach
    void setUp(){
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        setupTestData();
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("PUT /api/rides/{rideId}/stop - Success")
    void stopRide_integrationSuccess() throws Exception{
        StopRideRequestDTO req = new StopRideRequestDTO(
                LocalDateTime.now(),
                45.2671,
                19.8335
        );

        mockMvc.perform(put("/api/rides/{rideID}/stop", ongoingRideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.driverID").value(driver.getId()))
                .andExpect(jsonPath("$.status").value("FINISHED"))
                .andExpect(jsonPath("$.price").isNumber())
                .andExpect(jsonPath("$.price").value(greaterThan(0.0)))
                .andExpect(jsonPath("$.route.endAddress").isNotEmpty())
                .andExpect(jsonPath("$.endTime").isNotEmpty());

        Driver updatedDriver = driverRepository.findById(driver.getId()).orElseThrow();
        assert updatedDriver.getStatus() == DriverStatus.AVAILABLE;

        Ride updatedRide = rideRepository.findById(ongoingRideId).orElseThrow();
        assert updatedRide.getStatus() == RideStatus.FINISHED;
        assert updatedRide.getPrice() > 0;
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("PUT /api/rides/{rideId}/stop - Ride not found")
    void stopRide_rideNotFoundException() throws Exception{
        StopRideRequestDTO req = new StopRideRequestDTO(
                LocalDateTime.now(),
                45.99,
                25.99
        );

        mockMvc.perform(put("/api/rides/{rideID}/stop", -1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("PUT /api/rides/{rideId}/stop - FINISHED 400")
    void stopRide_stopRideException_rideNotOngoing() throws Exception {
        Ride finishedRide = Ride.builder()
                .driver(driver)
                .passenger(passenger)
                .route(ongoingRide.getRoute())
                .status(RideStatus.FINISHED)
                .isScheduled(false)
                .startTime(LocalDateTime.now().minusHours(1))
                .endTime(LocalDateTime.now().minusMinutes(30))
                .price(350.0)
                .build();
        finishedRide = rideRepository.save(finishedRide);

        StopRideRequestDTO request = new StopRideRequestDTO(
                LocalDateTime.now(),
                45.2671,
                19.8335
        );

        mockMvc.perform(put("/api/rides/{rideID}/stop", finishedRide.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("PUT /api/rides/{rideId}/stop - ACCEPTED 400")
    void stopRide_stopRideException_rideCancelled() throws Exception {
        Ride cancelledRide = Ride.builder()
                .driver(driver)
                .passenger(passenger)
                .route(ongoingRide.getRoute())
                .status(RideStatus.ACCEPTED)
                .isScheduled(false)
                .build();
        cancelledRide = rideRepository.save(cancelledRide);

        StopRideRequestDTO request = new StopRideRequestDTO(
                LocalDateTime.now(),
                45.2671,
                19.8335
        );

        mockMvc.perform(put("/api/rides/{rideID}/stop", cancelledRide.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "REGISTERED_USER")
    @DisplayName("PUT /api/rides/{rideId}/stop - Forbidden")
    void stopRide_forbidden_registeredUser() throws Exception{
        StopRideRequestDTO req = new StopRideRequestDTO(
                LocalDateTime.now(),
                45.99,
                25.99
        );

        mockMvc.perform(put("/api/rides/{rideID}/stop", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/rides/{rideId}/stop - Forbidden")
    void stopRide_forbidden_admin() throws Exception{
        StopRideRequestDTO req = new StopRideRequestDTO(
                LocalDateTime.now(),
                45.99,
                25.99
        );

        mockMvc.perform(put("/api/rides/{rideID}/stop", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /api/rides/{rideId}/stop - Unauthorized")
    void stopRide_unauthorized() throws Exception{
        StopRideRequestDTO req = new StopRideRequestDTO(
                LocalDateTime.now(),
                45.99,
                25.99
        );

        mockMvc.perform(put("/api/rides/{rideID}/stop", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("PUT /api/rides/{rideId}/stop - Invalid data 400")
    void stopRide_InvalidData() throws Exception {
        StopRideRequestDTO invalidRequest = new StopRideRequestDTO(
                null,
                45.2671,
                19.8335
        );

        mockMvc.perform(put("/api/rides/{rideID}/stop", ongoingRideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("PUT /api/rides/{rideId}/stop - Invalid coordinates 500")
    void stopRide_InvalidCoordinates() throws Exception {
        StopRideRequestDTO invalidRequest = new StopRideRequestDTO(
                LocalDateTime.now(),
                200.0,
                300.0
        );

        mockMvc.perform(put("/api/rides/{rideID}/stop", ongoingRideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    @DisplayName("PUT /api/rides/{rideId}/stop - Same coordinated 200")
    void stopRide_success_withoutDriver() throws Exception {
        StopRideRequestDTO req = new StopRideRequestDTO(
                LocalDateTime.now(),
                45.2551,
                19.8451
        );

        mockMvc.perform(put("/api/rides/{rideID}/stop", ongoingRideId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
