package org.example.vroom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.aspectj.lang.annotation.Before;
import org.example.vroom.DTOs.requests.ride.RideRequestDTO;
import org.example.vroom.DTOs.requests.ride.StopRideRequestDTO;
import org.example.vroom.DTOs.responses.ride.GetRideResponseDTO;
import org.example.vroom.DTOs.responses.route.GetRouteResponseDTO;
import org.example.vroom.DTOs.responses.route.PointResponseDTO;
import org.example.vroom.controllers.RideController;
import org.example.vroom.entities.*;
import org.example.vroom.enums.DriverStatus;
import org.example.vroom.enums.Gender;
import org.example.vroom.enums.RideStatus;
import org.example.vroom.enums.VehicleType;
import org.example.vroom.exceptions.user.NoAvailableDriverException;
import org.example.vroom.repositories.DriverRepository;
import org.example.vroom.repositories.RegisteredUserRepository;
import org.example.vroom.repositories.RideRepository;
import org.example.vroom.services.GeoService;
import org.example.vroom.services.RouteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class RideControllerTest {
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

    private Driver driver;
    private RegisteredUser passenger;
    private Ride ongoingRide;
    private Long ongoingRideId;

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

    ////////ORDER A RIDE/////////////////////////////////
    ///
    ///
    ///
    private GetRouteResponseDTO createValidRouteDTO() {
        return GetRouteResponseDTO.builder()
                .startLocationLat(45.2551)
                .startLocationLng(19.8451)
                .endLocationLat(45.2671)
                .endLocationLng(19.8335)
                .startAddress("Start")
                .endAddress("End")
                .stops(new ArrayList<>())
                .build();
    }

    private RideRequestDTO validRideRequest() {
        RideRequestDTO req = new RideRequestDTO();
        req.setVehicleType(VehicleType.STANDARD);
        req.setBabiesAllowed(false);
        req.setPetsAllowed(false);
        req.setScheduled(false);
        req.setRoute(createValidRouteDTO());
        return req;
    }


    @Test
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_success_integration() throws Exception {

        driver.setStatus(DriverStatus.AVAILABLE);
        driver = driverRepository.saveAndFlush(driver);

        RideRequestDTO req = new RideRequestDTO();
        req.setVehicleType(VehicleType.STANDARD);
        req.setBabiesAllowed(false);
        req.setPetsAllowed(false);
        req.setRoute(createValidRouteDTO());

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rideID").exists())
                .andExpect(jsonPath("$.price").isNumber());
    }

    @Test
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_noAvailableDriver() throws Exception {

        driver.setStatus(DriverStatus.UNAVAILABLE);
        driverRepository.save(driver);

        RideRequestDTO req = validRideRequest();


        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(containsString("No available drivers")));
    }

    @Test
    void orderRide_unauthorized() throws Exception {
        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockUser(roles = "REGISTERED_USER")
    void orderRide_badRequest() throws Exception {
        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_emptyStartLocation_badRequest() throws Exception {

        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.saveAndFlush(driver);

        GetRouteResponseDTO route = GetRouteResponseDTO.builder()
                .startLocationLat(null)
                .startLocationLng(null)
                .startAddress(null)
                .endLocationLat(45.2671)
                .endLocationLng(19.8335)
                .endAddress("End")
                .stops(new ArrayList<>())
                .build();

        RideRequestDTO req = new RideRequestDTO();
        req.setVehicleType(VehicleType.STANDARD);
        req.setBabiesAllowed(false);
        req.setPetsAllowed(false);
        req.setScheduled(false);
        req.setRoute(route);

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_luxury_withBabiesAndPets_success() throws Exception {

        driver.setStatus(DriverStatus.AVAILABLE);
        driver.getVehicle().setType(VehicleType.LUXURY);
        driver.getVehicle().setBabiesAllowed(true);
        driver.getVehicle().setPetsAllowed(true);
        driverRepository.saveAndFlush(driver);

        RideRequestDTO req = validRideRequest();
        req.setVehicleType(VehicleType.LUXURY);
        req.setBabiesAllowed(true);
        req.setPetsAllowed(true);

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rideID").exists())
                .andExpect(jsonPath("$.price").isNumber());
    }

    @Test
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_tooManyPassengers_conflict() throws Exception {

        driver.setStatus(DriverStatus.AVAILABLE);
        driver.getVehicle().setNumberOfSeats(4);
        driverRepository.saveAndFlush(driver);

        RideRequestDTO req = validRideRequest();
        req.setPassengersEmails(List.of(
                "p1@test.com",
                "p2@test.com",
                "p3@test.com",
                "p4@test.com"
        ));

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString("Vehicle capacity exceeded")));
    }
    @Test
    @Transactional
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_scheduled_success() throws Exception {
        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.saveAndFlush(driver);

        RideRequestDTO req = validRideRequest();
        req.setScheduled(true);
        req.setScheduledTime(LocalDateTime.now().plusHours(1));

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rideID").exists())
                .andExpect(jsonPath("$.scheduledTime").exists());
    }
    @Test
    @Transactional
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_scheduledInPast_badRequest() throws Exception {
        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.saveAndFlush(driver);

        RideRequestDTO req = validRideRequest();
        req.setScheduled(true);
        req.setScheduledTime(LocalDateTime.now().minusMinutes(10));

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
    @Test
    @Transactional
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_scheduledTooFar_badRequest() throws Exception {
        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.saveAndFlush(driver);

        RideRequestDTO req = validRideRequest();
        req.setScheduled(true);
        req.setScheduledTime(LocalDateTime.now().plusHours(10));

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
    @Test
    @Transactional
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_noExtraPassengers_success() throws Exception {
        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.saveAndFlush(driver);

        RideRequestDTO req = validRideRequest();
        req.setPassengersEmails(Collections.emptyList());

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rideID").exists());
    }
    @Test
    @Transactional
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_nullRoute_badRequest() throws Exception {
        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.saveAndFlush(driver);

        RideRequestDTO req = validRideRequest();
        req.setRoute(null);

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
    @Test
    @Transactional
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_babiesNotAllowed_noDriverConflict() throws Exception {
        driver.setStatus(DriverStatus.AVAILABLE);
        driver.getVehicle().setBabiesAllowed(false);
        driverRepository.saveAndFlush(driver);

        RideRequestDTO req = validRideRequest();
        req.setBabiesAllowed(true);

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(containsString("No available drivers")));
    }
    @Test
    @Transactional
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_petsNotAllowed_noDriverConflict() throws Exception {
        driver.setStatus(DriverStatus.AVAILABLE);
        driver.getVehicle().setPetsAllowed(false);
        driverRepository.saveAndFlush(driver);

        RideRequestDTO req = validRideRequest();
        req.setPetsAllowed(true);

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(containsString("No available drivers")));
    }
    @Test
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_scheduledInFuture_success() throws Exception {
        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.saveAndFlush(driver);

        RideRequestDTO req = validRideRequest();
        req.setScheduled(true);
        req.setScheduledTime(LocalDateTime.now().plusHours(2));

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rideID").exists())
                .andExpect(jsonPath("$.scheduledTime").exists())
                .andExpect(jsonPath("$.price").isNumber());
    }
    @Test
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_maxPassengers_success() throws Exception {
        driver.setStatus(DriverStatus.AVAILABLE);
        driver.getVehicle().setNumberOfSeats(5);
        driverRepository.saveAndFlush(driver);

        RideRequestDTO req = validRideRequest();
        req.setPassengersEmails(List.of("p1@test.com", "p2@test.com", "p3@test.com", "p4@test.com")); // + korisnik = 5

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rideID").exists())
                .andExpect(jsonPath("$.price").isNumber());
    }
    @Test
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_luxury_noBabiesOrPets_success() throws Exception {
        driver.setStatus(DriverStatus.AVAILABLE);
        driver.getVehicle().setType(VehicleType.LUXURY);
        driver.getVehicle().setBabiesAllowed(false);
        driver.getVehicle().setPetsAllowed(false);
        driverRepository.saveAndFlush(driver);

        RideRequestDTO req = validRideRequest();
        req.setVehicleType(VehicleType.LUXURY);
        req.setBabiesAllowed(false);
        req.setPetsAllowed(false);

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rideID").exists())
                .andExpect(jsonPath("$.price").isNumber());
    }
    @Test
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_emptyEndLocation_badRequest() throws Exception {
        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.saveAndFlush(driver);

        GetRouteResponseDTO route = GetRouteResponseDTO.builder()
                .startLocationLat(45.2551)
                .startLocationLng(19.8451)
                .endLocationLat(null)
                .endLocationLng(null)
                .startAddress("Start")
                .endAddress(null)
                .stops(new ArrayList<>())
                .build();

        RideRequestDTO req = validRideRequest();
        req.setRoute(route);

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
    @Test
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_nullPassengers_success() throws Exception {
        driver.setStatus(DriverStatus.AVAILABLE);
        driver.getVehicle().setNumberOfSeats(4);
        driverRepository.saveAndFlush(driver);

        RideRequestDTO req = validRideRequest();
        req.setPassengersEmails(null);

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rideID").exists())
                .andExpect(jsonPath("$.price").isNumber());
    }
    @Test
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_standardWithBabiesOrPets_noDriver() throws Exception {
        driver.setStatus(DriverStatus.AVAILABLE);
        driver.getVehicle().setType(VehicleType.STANDARD);
        driver.getVehicle().setBabiesAllowed(false);
        driver.getVehicle().setPetsAllowed(false);
        driverRepository.saveAndFlush(driver);

        RideRequestDTO req = validRideRequest();
        req.setVehicleType(VehicleType.STANDARD);
        req.setBabiesAllowed(true);
        req.setPetsAllowed(true);

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("No available drivers")));
    }
    @Test
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_nullStartAndEnd_badRequest() throws Exception {
        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.saveAndFlush(driver);

        GetRouteResponseDTO route = GetRouteResponseDTO.builder()
                .startLocationLat(null)
                .startLocationLng(null)
                .endLocationLat(null)
                .endLocationLng(null)
                .stops(new ArrayList<>())
                .build();

        RideRequestDTO req = validRideRequest();
        req.setRoute(route);

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
    @Test
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_scheduledExactly5Hours_success() throws Exception {
        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.saveAndFlush(driver);

        RideRequestDTO req = validRideRequest();
        req.setScheduled(true);
        req.setScheduledTime(LocalDateTime.now().plusHours(5));

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rideID").exists())
                .andExpect(jsonPath("$.scheduledTime").exists());
    }
    @Test
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_sameStartAndEnd_badRequest() throws Exception {
        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.saveAndFlush(driver);

        GetRouteResponseDTO route = GetRouteResponseDTO.builder()
                .startLocationLat(45.2551)
                .startLocationLng(19.8451)
                .endLocationLat(45.2551)
                .endLocationLng(19.8451)
                .startAddress("Start")
                .endAddress("End")
                .stops(new ArrayList<>())
                .build();

        RideRequestDTO req = validRideRequest();
        req.setRoute(route);

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Start and end locations cannot be the same"));
    }

    @Test
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_luxuryRequested_butDriverStandard_conflict() throws Exception {
        driver.setStatus(DriverStatus.AVAILABLE);
        driver.getVehicle().setType(VehicleType.STANDARD);
        driverRepository.saveAndFlush(driver);

        RideRequestDTO req = validRideRequest();
        req.setVehicleType(VehicleType.LUXURY);

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("No available drivers")));
    }

    @Test
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_duplicatePassengerEmails_ignored() throws Exception {
        driver.setStatus(DriverStatus.AVAILABLE);
        driver.getVehicle().setNumberOfSeats(4);
        driverRepository.saveAndFlush(driver);

        RideRequestDTO req = validRideRequest();
        req.setPassengersEmails(List.of("p1@test.com","p1@test.com","p2@test.com"));

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rideID").exists())
                .andExpect(jsonPath("$.passengers.length()").value(2))
                .andExpect(jsonPath("$.passengers[0]").value("p1@test.com"))
                .andExpect(jsonPath("$.passengers[1]").value("p2@test.com"));
    }

    @Test
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_withStops_success() throws Exception {
        driver.setStatus(DriverStatus.AVAILABLE);
        driver.getVehicle().setNumberOfSeats(4);
        driverRepository.saveAndFlush(driver);

        GetRouteResponseDTO route = GetRouteResponseDTO.builder()
                .startLocationLat(45.2551)
                .startLocationLng(19.8451)
                .endLocationLat(45.2671)
                .endLocationLng(19.8335)
                .startAddress("Start")
                .endAddress("End")
                .stops(List.of(
                        new PointResponseDTO(45.2560, 19.8460),
                        new PointResponseDTO(45.2600, 19.8400)
                ))
                .build();

        RideRequestDTO req = validRideRequest();
        req.setRoute(route);

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rideID").exists())
                .andExpect(jsonPath("$.route.stops.length()").value(2));
    }
}
