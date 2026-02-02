package org.example.vroom.service;

import org.example.vroom.DTOs.requests.ride.StopRideRequestDTO;
import org.example.vroom.DTOs.responses.ride.StoppedRideResponseDTO;
import org.example.vroom.DTOs.responses.route.RouteQuoteResponseDTO;
import org.example.vroom.entities.Driver;
import org.example.vroom.entities.RegisteredUser;
import org.example.vroom.entities.Ride;
import org.example.vroom.entities.Route;
import org.example.vroom.enums.DriverStatus;
import org.example.vroom.enums.Gender;
import org.example.vroom.enums.RideStatus;
import org.example.vroom.exceptions.ride.RideNotFoundException;
import org.example.vroom.exceptions.ride.StopRideException;
import org.example.vroom.mappers.RideMapper;
import org.example.vroom.repositories.DriverRepository;
import org.example.vroom.repositories.RideRepository;
import org.example.vroom.repositories.RouteRepository;
import org.example.vroom.services.RideService;
import org.example.vroom.services.RouteService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class RideServiceTest {
    @Mock
    private RideRepository rideRepository;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private RouteService routeService;
    @Mock
    private RideMapper rideMapper;

    @InjectMocks
    private RideService rideService;

    private static Driver createDriver() {
        return Driver.builder()
                .firstName("Marko").lastName("Markovic")
                .email("marko@vroom.com")
                .password("pass").address("Bulevar 1")
                .phoneNumber("060123").gender(Gender.MALE)
                .status(DriverStatus.AVAILABLE)
                .ratingCount(0L)
                .ratingSum(0L)
                .build();
    }

    private static RegisteredUser createPassenger() {
        return RegisteredUser.builder()
                .firstName("Jovan").lastName("Jovic")
                .email("jovan@vroom.com")
                .password("pass").address("Ulica 2")
                .phoneNumber("061987").gender(Gender.MALE)
                .build();
    }

    private static Route createRoute() {
        Route route = new Route();
        route.setStartLocationLat(45.25);
        route.setStartLocationLng(19.85);
        route.setEndLocationLat(45.26);
        route.setEndLocationLng(19.86);
        route.setStops(new ArrayList<>());
        return route;
    }

    private static StopRideRequestDTO createStopRideRequest(){
        return new StopRideRequestDTO(
                LocalDateTime.now(),
                45.26,
                19.86
        );
    }

    @Test
    @DisplayName("Ride not found - invalid rideId")
    @Tag("exception-handling")
    void stopRide_rideNotFoundException_invalidRideId(){
        when(rideRepository.findById(-1L)).thenReturn(Optional.empty());

        StopRideRequestDTO req = new StopRideRequestDTO(
                LocalDateTime.now(),
                45.2671,
                19.8335
        );

        RideNotFoundException e = assertThrows(RideNotFoundException.class, () -> rideService.stopRide(-1L, req));
        assertEquals("Ride not found", e.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Stop ride exception - invalid ride status")
    @EnumSource(value = RideStatus.class, names = {"ONGOING"}, mode = EnumSource.Mode.EXCLUDE)
    @Tag("exception-handling")
    void stopRide_stopRideException_invalidRideStatus(RideStatus status){
        Driver d1 = createDriver();
        RegisteredUser p1 = createPassenger();
        Route ru1 = createRoute();

        Ride ride = Ride.builder()
                .driver(d1)
                .passenger(p1)
                .route(ru1)
                .status(status)
                .build();

        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

        StopRideRequestDTO req = new StopRideRequestDTO(
                LocalDateTime.now(),
                45.2671,
                19.8335
        );

        StopRideException e = assertThrows(StopRideException.class, () -> rideService.stopRide(1L, req));
        assertEquals("Ride must be active in order to stop it", e.getMessage());
    }

    @Test
    @DisplayName("Stopped ride success - driver is present")
    void stopRide_stoppedRideResponseDTO_withDriver(){
        Driver d1 = createDriver();
        RegisteredUser p1 = createPassenger();
        Route ru1 = createRoute();

        Ride ride = Ride.builder()
                .driver(d1)
                .passenger(p1)
                .route(ru1)
                .status(RideStatus.ONGOING)
                .build();

        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        StopRideRequestDTO req = createStopRideRequest();

        RouteQuoteResponseDTO routeQuote = new RouteQuoteResponseDTO();
        routeQuote.setPrice(1500.0);
        when(routeService.routeEstimation(anyString(), anyString())).thenReturn(routeQuote);

        StoppedRideResponseDTO res = new StoppedRideResponseDTO();
        when(rideMapper.stopRide(any(), any(), anyDouble(), anyString())).thenReturn(res);

        StoppedRideResponseDTO result = rideService.stopRide(1L, req);

        assertEquals(RideStatus.FINISHED, ride.getStatus());
        assertEquals(DriverStatus.AVAILABLE, d1.getStatus());
        assertEquals(1500.0, ride.getPrice());

        verify(rideRepository).save(ride);
        verify(driverRepository).save(d1);
    }

    @Test
    @DisplayName("Stopped ride success - driver is not present")
    void stopRide_stoppedRideResponseDTO_noDriver(){
        RegisteredUser p1 = createPassenger();
        Route ru1 = createRoute();

        Ride ride = Ride.builder()
                .driver(null)
                .passenger(p1)
                .route(ru1)
                .status(RideStatus.ONGOING)
                .build();

        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

        RouteQuoteResponseDTO routeQuote = new RouteQuoteResponseDTO();
        routeQuote.setPrice(1500.0);
        when(routeService.routeEstimation(anyString(), anyString())).thenReturn(routeQuote);

        StoppedRideResponseDTO res = new StoppedRideResponseDTO();
        when(rideMapper.stopRide(any(), any(), anyDouble(), anyString())).thenReturn(res);

        StopRideRequestDTO req = createStopRideRequest();
        rideService.stopRide(1L, req);

        assertEquals(RideStatus.FINISHED, ride.getStatus());
        verify(driverRepository, never()).save(any(Driver.class));
        verify(rideRepository).save(ride);
    }


    @Test
    @DisplayName("Stopped ride success - same end coordinated")
    void stopRide_stoppedRideResponseDTO_sameEndCoordinated(){
        RegisteredUser p1 = createPassenger();
        Route ru1 =  Route.builder()
                .startLocationLat(45.25)
                .startLocationLng(19.85)
                .endLocationLat(45.26)
                .endLocationLng(19.86)
                .stops(new ArrayList<>())
                .build();

        Ride ride = Ride.builder()
                .driver(createDriver())
                .passenger(p1)
                .route(ru1)
                .status(RideStatus.ONGOING)
                .build();

        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

        RouteQuoteResponseDTO routeQuote = new RouteQuoteResponseDTO();
        routeQuote.setPrice(1500.0);
        when(routeService.routeEstimation(anyString(), anyString())).thenReturn(routeQuote);

        StoppedRideResponseDTO res = new StoppedRideResponseDTO();
        when(rideMapper.stopRide(any(), any(), anyDouble(), anyString())).thenReturn(res);

        StopRideRequestDTO req = createStopRideRequest();
        rideService.stopRide(1L, req);

        assertEquals(RideStatus.FINISHED, ride.getStatus());
        verify(driverRepository).save(any(Driver.class));
        verify(rideRepository).save(ride);
    }


    @Test
    @DisplayName("Stopped ride success - invalid coordinates")
    @Tag("exception-handling")
    void stopRide_exception_invalidCoordinated(){
        RegisteredUser p1 = createPassenger();
        Route ru1 =  Route.builder()
                .startLocationLat(45.25)
                .startLocationLng(19.85)
                .endLocationLat(45.26)
                .endLocationLng(19.86)
                .stops(new ArrayList<>())
                .build();


        StopRideRequestDTO req = new StopRideRequestDTO(LocalDateTime.now(), 999.0, 999.0);
        StopRideException e = assertThrows(StopRideException.class, () -> rideService.stopRide(1L, req));
    }

    @Test
    @DisplayName("Stopped ride success - invalid time")
    @Tag("exception-handling")
    void stopRide_exception_invalidData(){
        RegisteredUser p1 = createPassenger();
        Route ru1 =  Route.builder()
                .startLocationLat(45.25)
                .startLocationLng(19.85)
                .endLocationLat(45.26)
                .endLocationLng(19.86)
                .stops(new ArrayList<>())
                .build();

        Ride ride = Ride.builder()
                .driver(null)
                .passenger(p1)
                .route(ru1)
                .status(RideStatus.ONGOING)
                .build();

        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

        StopRideRequestDTO invalidReq = new StopRideRequestDTO(
                null,
                45.2671,
                19.8335
        );
        NullPointerException e = assertThrows(NullPointerException.class, () -> rideService.stopRide(1L, invalidReq));
    }


}
