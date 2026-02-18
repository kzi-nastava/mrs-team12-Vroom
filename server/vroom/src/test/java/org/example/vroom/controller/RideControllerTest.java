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
import org.example.vroom.repositories.*;
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
    @Autowired
    private DriverLocationRepository driverLocationRepository;
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
    private DriverLocation createDriverLocation(Driver driver, Double lat, Double lng) {
        DriverLocation location = DriverLocation.builder()
                .driver(driver)
                .latitude(lat)
                .longitude(lng)
                .lastUpdated(LocalDateTime.now())
                .build();
        return driverLocationRepository.save(location);
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

        DriverLocation location = createDriverLocation(driver, 45.2551, 19.8451);
        driverLocationRepository.save(location);

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
    void orderRide_blockedUser_forbidden() throws Exception {
        passenger.setBlockedReason("Inappropriate behavior");
        registeredUserRepository.saveAndFlush(passenger);

        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.saveAndFlush(driver);

        RideRequestDTO req = validRideRequest();

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message")
                        .value(containsString("Inappropriate behavior")));
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

    @Test
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_driverHasScheduledRideInNext15Minutes_conflict() throws Exception {

        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.saveAndFlush(driver);

        Ride scheduledRide = Ride.builder()
                .driver(driver)
                .passenger(passenger)
                .route(createRoute())
                .status(RideStatus.ACCEPTED)
                .isScheduled(true)
                .startTime(LocalDateTime.now().plusMinutes(10))
                .price(100.0)
                .build();

        rideRepository.saveAndFlush(scheduledRide);

        RideRequestDTO req = validRideRequest();

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value(containsString("Driver has a scheduled ride at")));
    }
    @Test
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_scheduledExactlyIn15Minutes_conflict() throws Exception {

        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.saveAndFlush(driver);

        Ride scheduledRide = Ride.builder()
                .driver(driver)
                .passenger(passenger)
                .route(createRoute())
                .status(RideStatus.ACCEPTED)
                .isScheduled(true)
                .startTime(LocalDateTime.now().plusMinutes(15))
                .price(100.0)
                .build();

        rideRepository.saveAndFlush(scheduledRide);

        RideRequestDTO req = validRideRequest();

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }
    @Test
    @WithMockUser(username = "jovan.passenger@test.com", roles = "REGISTERED_USER")
    void orderRide_scheduledAfter15Minutes_success() throws Exception {

        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.saveAndFlush(driver);

        Ride scheduledRide = Ride.builder()
                .driver(driver)
                .passenger(passenger)
                .route(createRoute())
                .status(RideStatus.ACCEPTED)
                .isScheduled(true)
                .startTime(LocalDateTime.now().plusMinutes(16))
                .price(100.0)
                .build();

        rideRepository.saveAndFlush(scheduledRide);

        RideRequestDTO req = validRideRequest();

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }
}
