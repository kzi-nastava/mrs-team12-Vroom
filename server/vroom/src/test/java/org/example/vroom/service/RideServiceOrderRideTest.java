package org.example.vroom.service;

import org.example.vroom.DTOs.requests.ride.RideRequestDTO;

import org.example.vroom.DTOs.responses.ride.GetRideResponseDTO;
import org.example.vroom.DTOs.responses.route.GetRouteResponseDTO;
import org.example.vroom.DTOs.responses.route.PointResponseDTO;
import org.example.vroom.DTOs.responses.route.RouteQuoteResponseDTO;
import org.example.vroom.entities.*;
import org.example.vroom.enums.DriverStatus;
import org.example.vroom.enums.Gender;
import org.example.vroom.enums.RideStatus;
import org.example.vroom.enums.VehicleType;

import org.example.vroom.exceptions.ride.DriverNotAvailableException;
import org.example.vroom.exceptions.ride.NoAvailableDriverException;
import org.example.vroom.exceptions.ride.TooManyPassengersException;
import org.example.vroom.exceptions.user.UserNotFoundException;
import org.example.vroom.mappers.RideMapper;
import org.example.vroom.mappers.RouteMapper;
import org.example.vroom.repositories.*;
import org.example.vroom.services.RideService;
import org.example.vroom.services.RouteService;
import org.example.vroom.utils.EmailService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.format.ResolverStyle.LENIENT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RideServiceOrderRideTest {

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

    @Mock
    private RouteMapper routeMapper;

    @Mock
    private RegisteredUserRepository userRepository;

    @Mock
    private RouteRepository routeRepository;
    @Mock
    private PriceListRepository pricelistRepository;

    @Mock
    private EmailService emailService;


    private Driver driver;
    private RegisteredUser passenger;


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
    private void mockValidPricelist() {
        Pricelist pricelist = Pricelist.builder()
                .valid(true)
                .priceStandard(100)
                .priceLuxury(200)
                .priceMinivan(150)
                .build();

        lenient().when(pricelistRepository.findByValidTrue())
                .thenReturn(Optional.of(pricelist));
    }
    @BeforeEach
    void setup() {
        Vehicle vehicle = Vehicle.builder()
                .brand("Toyota")
                .model("Corolla")
                .type(VehicleType.STANDARD)
                .licenceNumber("NS123AB")
                .numberOfSeats(4)
                .babiesAllowed(false)
                .petsAllowed(false)
                .build();
        driver = Driver.builder()
                .firstName("Marko").lastName("Markovic")
                .email("marko@vroom.com")
                .password("password123")
                .address("Bulevar Oslobodjenja 1")
                .phoneNumber("0601234567")
                .gender(Gender.MALE)
                .status(DriverStatus.AVAILABLE)
                .ratingCount(0L)
                .ratingSum(0L)
                .vehicle(vehicle)
                .build();

        passenger = RegisteredUser.builder()
                .firstName("Jovan").lastName("Jovic")
                .email("jovan@vroom.com")
                .build();
    }
    private static RideRequestDTO createValidRideRequest() {
        RideRequestDTO req = new RideRequestDTO();
        req.setVehicleType(VehicleType.STANDARD);
        req.setBabiesAllowed(false);
        req.setPetsAllowed(false);
        req.setScheduled(false);
        return req;
    }

    private GetRouteResponseDTO createRoute(double startLat, double startLng, double endLat, double endLng) {
        return GetRouteResponseDTO.builder()
                .startLocationLat(startLat)
                .startLocationLng(startLng)
                .endLocationLat(endLat)
                .endLocationLng(endLng)
                .startAddress("Start")
                .endAddress("End")
                .stops(new ArrayList<>())
                .build();
    }
    private void mockBaseSuccess() {


        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(passenger));

        when(driverRepository.findFirstAvailableDriver(any(), any(), any(), any(), any()))
                .thenReturn(Optional.of(driver));

        when(rideRepository.saveAndFlush(any()))
                .thenAnswer(i -> i.getArguments()[0]);

        when(rideMapper.getRideDTO(any()))
                .thenReturn(new GetRideResponseDTO());

        when(routeRepository.save(any(Route.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        when(routeMapper.fromDTO(any()))
                .thenAnswer(invocation -> {
                    GetRouteResponseDTO dto = invocation.getArgument(0);
                    Route r = new Route();
                    r.setStartLocationLat(dto.getStartLocationLat());
                    r.setStartLocationLng(dto.getStartLocationLng());
                    r.setEndLocationLat(dto.getEndLocationLat());
                    r.setEndLocationLng(dto.getEndLocationLng());
                    r.setStops(new ArrayList<>());
                    return r;
                });
    }
    @Test
    @DisplayName("Order ride fails - user not found")
    void orderRide_userNotFound() {
        // arrange
        RideRequestDTO req = createValidRideRequest();

        when(userRepository.findByEmail("missing@test.com"))
                .thenReturn(Optional.empty());

        // act and assert
        UserNotFoundException ex = assertThrows(
                UserNotFoundException.class,
                () -> rideService.orderRide("missing@test.com", req)
        );

        assertEquals("User not found", ex.getMessage());

        verify(driverRepository, never()).findFirstAvailableDriver(any(), any(), any(), any(), any());
        verify(rideRepository, never()).save(any());
    }

    @Test
    @DisplayName("Order ride - not scheduled ignores scheduledTime")
    void orderRide_notScheduled() {
        GetRouteResponseDTO routeDTO = createRoute(45, 19, 46, 20);

        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(false)
                .scheduledTime(null)
                .route(routeDTO)
                .build();

        mockBaseSuccess();

        when(routeService.routeEstimation(anyString(), anyString(), nullable(String.class)))
                .thenReturn(new RouteQuoteResponseDTO(100.0, 30.0));

        GetRideResponseDTO res = rideService.orderRide(passenger.getEmail(), req);

        assertNotNull(res);
    }
    @Test
    @DisplayName("Order ride - scheduled time in the past throws exception")
    void orderRide_scheduledTimeInPast() {
        GetRouteResponseDTO routeDTO = createRoute(45, 19, 46, 20);

        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(true)
                .scheduledTime(LocalDateTime.now().minusHours(1))
                .route(routeDTO)
                .build();


        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(passenger));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> rideService.orderRide(passenger.getEmail(), req)
        );

        assertEquals("Scheduled time cannot be in the past", ex.getMessage());
    }

    @Test
    @DisplayName("Order ride - scheduled time more than 5 hours ahead throws exception")
    void orderRide_scheduledTimeTooFarAhead() {
        GetRouteResponseDTO routeDTO = createRoute(45, 19, 46, 20);

        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(true)
                .scheduledTime(LocalDateTime.now().plusHours(6))
                .route(routeDTO)
                .build();

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(passenger));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> rideService.orderRide(passenger.getEmail(), req)
        );

        assertEquals("Scheduled ride cannot be more than 5 hours ahead", ex.getMessage());
    }

    @Test
    @DisplayName("Order ride - valid scheduled time succeeds")
    void orderRide_validScheduledTime() {
        GetRouteResponseDTO routeDTO = createRoute(45, 19, 46, 20);
        LocalDateTime validScheduledTime = LocalDateTime.now().plusHours(2); // 2 hours ahead (valid)

        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(true)
                .scheduledTime(validScheduledTime)
                .route(routeDTO)
                .build();

        mockBaseSuccess();
        when(routeService.routeEstimation(anyString(), anyString(), nullable(String.class)))
                .thenReturn(new RouteQuoteResponseDTO(100.0, 30.0));

        GetRideResponseDTO res = rideService.orderRide(passenger.getEmail(), req);

        assertNotNull(res);
        verify(rideRepository).saveAndFlush(any(Ride.class));
    }

    @Test
    @DisplayName("Order ride - scheduled false with scheduledTime provided ignores time")
    void orderRide_scheduledFalseIgnoresTime() {
        GetRouteResponseDTO routeDTO = createRoute(45, 19, 46, 20);

        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(false)
                .scheduledTime(LocalDateTime.now().plusHours(2))
                .route(routeDTO)
                .build();

        mockBaseSuccess();
        when(routeService.routeEstimation(anyString(), anyString(), nullable(String.class)))
                .thenReturn(new RouteQuoteResponseDTO(100.0, 30.0));

        GetRideResponseDTO res = rideService.orderRide(passenger.getEmail(), req);

        assertNotNull(res);
    }
    @Test
    @DisplayName("Order ride successfully with available driver")
    void orderRide_success() {
        GetRouteResponseDTO routeDTO = createRoute(45.25, 19.85, 45.26, 19.86);

        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(false)
                .route(routeDTO)
                .build();

        mockBaseSuccess();

        when(routeService.routeEstimation(anyString(), anyString(), nullable(String.class)))
                .thenReturn(new RouteQuoteResponseDTO(100.0, 30.0));

        GetRideResponseDTO response = rideService.orderRide(passenger.getEmail(), req);

        assertNotNull(response);
        verify(rideRepository).saveAndFlush(any(Ride.class));
        assertEquals(DriverStatus.UNAVAILABLE, driver.getStatus());
    }

    @Test
    @DisplayName("Order ride - filters out null and blank passenger emails")
    void orderRide_filtersNullAndBlankEmails() {
        GetRouteResponseDTO routeDTO = createRoute(45.25, 19.85, 45.26, 19.86);

        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(false)
                .route(routeDTO)
                .passengersEmails(Arrays.asList("valid@test.com", null, "", "  ", "another@test.com"))
                .build();

        mockBaseSuccess();

        when(routeService.routeEstimation(anyString(), anyString(), nullable(String.class)))
                .thenReturn(new RouteQuoteResponseDTO(100.0, 30.0));

        GetRideResponseDTO response = rideService.orderRide(passenger.getEmail(), req);

        assertNotNull(response);

        ArgumentCaptor<Ride> rideCaptor = ArgumentCaptor.forClass(Ride.class);
        verify(rideRepository).saveAndFlush(rideCaptor.capture());

        Ride savedRide = rideCaptor.getValue();
        assertEquals(2, savedRide.getPassengers().size());
        assertTrue(savedRide.getPassengers().contains("valid@test.com"));
        assertTrue(savedRide.getPassengers().contains("another@test.com"));
    }

    @Test
    @DisplayName("Order ride - null passengersEmails list works fine")
    void orderRide_nullPassengersList() {
        GetRouteResponseDTO routeDTO = createRoute(45.25, 19.85, 45.26, 19.86);

        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(false)
                .route(routeDTO)
                .passengersEmails(null)
                .build();

        mockBaseSuccess();

        when(routeService.routeEstimation(anyString(), anyString(), nullable(String.class)))
                .thenReturn(new RouteQuoteResponseDTO(100.0, 30.0));

        GetRideResponseDTO response = rideService.orderRide(passenger.getEmail(), req);

        assertNotNull(response);

        ArgumentCaptor<Ride> rideCaptor = ArgumentCaptor.forClass(Ride.class);
        verify(rideRepository).saveAndFlush(rideCaptor.capture());

        Ride savedRide = rideCaptor.getValue();
        assertEquals(0, savedRide.getPassengers().size());
    }

    @Test
    @DisplayName("Order ride fails - no available driver")
    void orderRide_noAvailableDriver() {
        GetRouteResponseDTO routeDTO = createRoute(45.25, 19.85, 45.26, 19.86);

        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(false)
                .route(routeDTO)
                .build();


        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(passenger));


        when(routeMapper.fromDTO(any()))
                .thenAnswer(invocation -> {
                    GetRouteResponseDTO dto = invocation.getArgument(0);
                    Route r = new Route();
                    r.setStartLocationLat(dto.getStartLocationLat());
                    r.setStartLocationLng(dto.getStartLocationLng());
                    r.setEndLocationLat(dto.getEndLocationLat());
                    r.setEndLocationLng(dto.getEndLocationLng());
                    r.setStops(new ArrayList<>());
                    return r;
                });

        when(routeRepository.save(any(Route.class)))
                .thenAnswer(i -> i.getArguments()[0]);


        when(driverRepository.findFirstAvailableDriver(any(), any(), any(), any(), any()))
                .thenReturn(Optional.empty());

        org.example.vroom.exceptions.user.NoAvailableDriverException e = assertThrows(
                org.example.vroom.exceptions.user.NoAvailableDriverException.class,
                () -> rideService.orderRide(passenger.getEmail(), req)
        );

        assertEquals("No available drivers", e.getMessage());
    }
    @Test
    @DisplayName("Order ride fails - too many passengers for vehicle capacity")
    void orderRide_tooManyPassengers() {
        GetRouteResponseDTO routeDTO = createRoute(45.25, 19.85, 45.26, 19.86);


        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(false)
                .route(routeDTO)
                .passengersEmails(Arrays.asList("p1@test.com", "p2@test.com", "p3@test.com", "p4@test.com"))
                .build();

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(passenger));

        when(routeMapper.fromDTO(any()))
                .thenAnswer(invocation -> {
                    GetRouteResponseDTO dto = invocation.getArgument(0);
                    Route r = new Route();
                    r.setStartLocationLat(dto.getStartLocationLat());
                    r.setStartLocationLng(dto.getStartLocationLng());
                    r.setEndLocationLat(dto.getEndLocationLat());
                    r.setEndLocationLng(dto.getEndLocationLng());
                    r.setStops(new ArrayList<>());
                    return r;
                });

        when(routeRepository.save(any(Route.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        when(driverRepository.findFirstAvailableDriver(any(), any(), any(), any(), any()))
                .thenReturn(Optional.of(driver));

        TooManyPassengersException ex = assertThrows(
                TooManyPassengersException.class,
                () -> rideService.orderRide(passenger.getEmail(), req)
        );

        assertEquals("Vehicle capacity exceeded: max 4, requested 5", ex.getMessage());
    }
    @Test
    @DisplayName("Order ride fails - same start and end")
    void orderRide_sameStartAndEnd() {
        GetRouteResponseDTO routeDTO = createRoute(45.25, 19.85, 45.25, 19.85);

        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(false)
                .route(routeDTO)
                .build();

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(passenger));

        when(routeMapper.fromDTO(any()))
                .thenAnswer(invocation -> {
                    GetRouteResponseDTO dto = invocation.getArgument(0);
                    Route r = new Route();
                    r.setStartLocationLat(dto.getStartLocationLat());
                    r.setStartLocationLng(dto.getStartLocationLng());
                    r.setEndLocationLat(dto.getEndLocationLat());
                    r.setEndLocationLng(dto.getEndLocationLng());
                    r.setStops(new ArrayList<>());
                    return r;
                });

        when(routeRepository.save(any(Route.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        when(driverRepository.findFirstAvailableDriver(any(), any(), any(), any(), any()))
                .thenReturn(Optional.of(driver));

        when(routeService.routeEstimation(anyString(), anyString(), nullable(String.class)))
                .thenReturn(new RouteQuoteResponseDTO(100.0, 30.0));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> rideService.orderRide(passenger.getEmail(), req));

        assertEquals("Start and end locations cannot be the same", e.getMessage());
    }

    @Test
    @DisplayName("Order ride ignores duplicate passengers")
    void orderRide_duplicatePassengers() {
        GetRouteResponseDTO routeDTO = createRoute(45.25, 19.85, 45.26, 19.86);

        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(false)
                .route(routeDTO)
                .passengersEmails(List.of("p1@test.com", "p1@test.com", "p2@test.com"))
                .build();

        mockBaseSuccess();

        when(routeService.routeEstimation(anyString(), anyString(), nullable(String.class)))
                .thenReturn(new RouteQuoteResponseDTO(100.0, 30.0));

        GetRideResponseDTO response = rideService.orderRide(passenger.getEmail(), req);

        assertNotNull(response);
        verify(rideRepository).saveAndFlush(any(Ride.class));
    }

    @Test
    @DisplayName("Order ride - with valid stops in route")
    void orderRide_withStops() {
        PointResponseDTO stop1 = new PointResponseDTO();
        stop1.setLat(45.251);
        stop1.setLng(19.851);

        PointResponseDTO stop2 = new PointResponseDTO();
        stop2.setLat(45.252);
        stop2.setLng(19.852);

        GetRouteResponseDTO routeDTO = GetRouteResponseDTO.builder()
                .startLocationLat(45.25)
                .startLocationLng(19.85)
                .endLocationLat(45.26)
                .endLocationLng(19.86)
                .startAddress("Start")
                .endAddress("End")
                .stops(Arrays.asList(stop1, stop2))
                .build();

        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(false)
                .route(routeDTO)
                .build();


        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(passenger));

        when(driverRepository.findFirstAvailableDriver(any(), any(), any(), any(), any()))
                .thenReturn(Optional.of(driver));

        when(rideRepository.saveAndFlush(any()))
                .thenAnswer(i -> i.getArguments()[0]);

        when(rideMapper.getRideDTO(any()))
                .thenReturn(new GetRideResponseDTO());

        when(routeRepository.save(any(Route.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        when(routeMapper.fromDTO(any()))
                .thenAnswer(invocation -> {
                    GetRouteResponseDTO dto = invocation.getArgument(0);
                    Route r = new Route();
                    r.setStartLocationLat(dto.getStartLocationLat());
                    r.setStartLocationLng(dto.getStartLocationLng());
                    r.setEndLocationLat(dto.getEndLocationLat());
                    r.setEndLocationLng(dto.getEndLocationLng());

                    if (dto.getStops() != null) {
                        List<Point> points = dto.getStops().stream()
                                .map(pointDTO -> {
                                    Point p = new Point();
                                    p.setLat(pointDTO.getLat());
                                    p.setLng(pointDTO.getLng());
                                    return p;
                                })
                                .collect(Collectors.toList());
                        r.setStops(points);
                    } else {
                        r.setStops(new ArrayList<>());
                    }

                    return r;
                });

        when(routeService.routeEstimation(
                eq("45.25,19.85"),
                eq("45.26,19.86"),
                eq("45.251,19.851;45.252,19.852")
        )).thenReturn(new RouteQuoteResponseDTO(150.0, 45.0));

        GetRideResponseDTO response = rideService.orderRide(passenger.getEmail(), req);

        assertNotNull(response);
        verify(routeService).routeEstimation(
                eq("45.25,19.85"),
                eq("45.26,19.86"),
                eq("45.251,19.851;45.252,19.852")
        );
    }

    @Test
    @DisplayName("Order ride - filters null stops from route")
    void orderRide_filtersNullStops() {
        PointResponseDTO validStop = new PointResponseDTO();
        validStop.setLat(45.251);
        validStop.setLng(19.851);

        PointResponseDTO invalidStop1 = null;

        PointResponseDTO invalidStop2 = new PointResponseDTO();
        invalidStop2.setLat(null);
        invalidStop2.setLng(19.852);

        GetRouteResponseDTO routeDTO = GetRouteResponseDTO.builder()
                .startLocationLat(45.25)
                .startLocationLng(19.85)
                .endLocationLat(45.26)
                .endLocationLng(19.86)
                .startAddress("Start")
                .endAddress("End")
                .stops(Arrays.asList(validStop, invalidStop1, invalidStop2))
                .build();

        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(false)
                .route(routeDTO)
                .build();

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(passenger));

        when(driverRepository.findFirstAvailableDriver(any(), any(), any(), any(), any()))
                .thenReturn(Optional.of(driver));

        when(rideRepository.saveAndFlush(any()))
                .thenAnswer(i -> i.getArguments()[0]);

        when(rideMapper.getRideDTO(any()))
                .thenReturn(new GetRideResponseDTO());

        when(routeRepository.save(any(Route.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        when(routeMapper.fromDTO(any()))
                .thenAnswer(invocation -> {
                    GetRouteResponseDTO dto = invocation.getArgument(0);
                    Route r = new Route();
                    r.setStartLocationLat(dto.getStartLocationLat());
                    r.setStartLocationLng(dto.getStartLocationLng());
                    r.setEndLocationLat(dto.getEndLocationLat());
                    r.setEndLocationLng(dto.getEndLocationLng());

                    if (dto.getStops() != null) {
                        List<Point> points = dto.getStops().stream()
                                .filter(pointDTO -> pointDTO != null)
                                .map(pointDTO -> {
                                    Point p = new Point();
                                    p.setLat(pointDTO.getLat());
                                    p.setLng(pointDTO.getLng());
                                    return p;
                                })
                                .collect(Collectors.toList());
                        r.setStops(points);
                    } else {
                        r.setStops(new ArrayList<>());
                    }

                    return r;
                });

        when(routeService.routeEstimation(
                eq("45.25,19.85"),
                eq("45.26,19.86"),
                eq("45.251,19.851")
        )).thenReturn(new RouteQuoteResponseDTO(120.0, 35.0));

        GetRideResponseDTO response = rideService.orderRide(passenger.getEmail(), req);

        assertNotNull(response);
        verify(routeService).routeEstimation(
                eq("45.25,19.85"),
                eq("45.26,19.86"),
                eq("45.251,19.851")
        );
    }
    @Test
    @DisplayName("Order ride - exactly at vehicle capacity succeeds")
    void orderRide_exactlyAtCapacity() {
        GetRouteResponseDTO routeDTO = createRoute(45.25, 19.85, 45.26, 19.86);

        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(false)
                .route(routeDTO)
                .passengersEmails(Arrays.asList("p1@test.com", "p2@test.com", "p3@test.com"))
                .build();

        mockBaseSuccess();

        when(routeService.routeEstimation(anyString(), anyString(), nullable(String.class)))
                .thenReturn(new RouteQuoteResponseDTO(100.0, 30.0));

        GetRideResponseDTO response = rideService.orderRide(passenger.getEmail(), req);

        assertNotNull(response);
        verify(rideRepository).saveAndFlush(any(Ride.class));
    }

    @Test
    @DisplayName("Order ride - scheduled exactly 5 hours ahead succeeds")
    void orderRide_scheduledExactly5Hours() {
        GetRouteResponseDTO routeDTO = createRoute(45, 19, 46, 20);
        LocalDateTime exactly5Hours = LocalDateTime.now().plusHours(5);

        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(true)
                .scheduledTime(exactly5Hours)
                .route(routeDTO)
                .build();

        mockBaseSuccess();

        when(routeService.routeEstimation(anyString(), anyString(), nullable(String.class)))
                .thenReturn(new RouteQuoteResponseDTO(100.0, 30.0));

        GetRideResponseDTO res = rideService.orderRide(passenger.getEmail(), req);

        assertNotNull(res);
        verify(rideRepository).saveAndFlush(any(Ride.class));
    }

    @Test
    @DisplayName("Order ride - empty stops list works")
    void orderRide_emptyStopsList() {
        GetRouteResponseDTO routeDTO = GetRouteResponseDTO.builder()
                .startLocationLat(45.25)
                .startLocationLng(19.85)
                .endLocationLat(45.26)
                .endLocationLng(19.86)
                .startAddress("Start")
                .endAddress("End")
                .stops(new ArrayList<>())
                .build();

        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(false)
                .route(routeDTO)
                .build();

        mockBaseSuccess();

        when(routeService.routeEstimation(anyString(), anyString(), nullable(String.class)))
                .thenReturn(new RouteQuoteResponseDTO(100.0, 30.0));

        GetRideResponseDTO response = rideService.orderRide(passenger.getEmail(), req);

        assertNotNull(response);
        verify(routeService).routeEstimation(
                eq("45.25,19.85"),
                eq("45.26,19.86"),
                isNull()
        );
    }

    @Test
    @DisplayName("Order ride - correct price from quote")
    void orderRide_correctPrice() {
        GetRouteResponseDTO routeDTO = createRoute(45.25, 19.85, 45.26, 19.86);

        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(false)
                .route(routeDTO)
                .build();

        mockBaseSuccess();

        double expectedPrice = 250.75;
        when(routeService.routeEstimation(anyString(), anyString(), nullable(String.class)))
                .thenReturn(new RouteQuoteResponseDTO(expectedPrice, 30.0));

        rideService.orderRide(passenger.getEmail(), req);

        ArgumentCaptor<Ride> rideCaptor = ArgumentCaptor.forClass(Ride.class);
        verify(rideRepository).saveAndFlush(rideCaptor.capture());

        Ride savedRide = rideCaptor.getValue();
        assertEquals(expectedPrice, savedRide.getPrice());
    }

    @Test
    @DisplayName("Order ride - driver status set to UNAVAILABLE")
    void orderRide_driverStatusSetToUnavailable() {
        GetRouteResponseDTO routeDTO = createRoute(45.25, 19.85, 45.26, 19.86);

        driver.setStatus(DriverStatus.AVAILABLE);

        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(false)
                .route(routeDTO)
                .build();

        mockBaseSuccess();

        when(routeService.routeEstimation(anyString(), anyString(), nullable(String.class)))
                .thenReturn(new RouteQuoteResponseDTO(100.0, 30.0));

        rideService.orderRide(passenger.getEmail(), req);

        assertEquals(DriverStatus.UNAVAILABLE, driver.getStatus());
    }

    @Test
    @DisplayName("Order ride - ride status is ACCEPTED")
    void orderRide_statusIsAccepted() {
        GetRouteResponseDTO routeDTO = createRoute(45.25, 19.85, 45.26, 19.86);

        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(false)
                .route(routeDTO)
                .build();

        mockBaseSuccess();

        when(routeService.routeEstimation(anyString(), anyString(), nullable(String.class)))
                .thenReturn(new RouteQuoteResponseDTO(100.0, 30.0));

        rideService.orderRide(passenger.getEmail(), req);

        ArgumentCaptor<Ride> rideCaptor = ArgumentCaptor.forClass(Ride.class);
        verify(rideRepository).saveAndFlush(rideCaptor.capture());

        Ride savedRide = rideCaptor.getValue();
        assertEquals(RideStatus.ACCEPTED, savedRide.getStatus());
    }

    @Test
    @DisplayName("Order ride - panic is not activated")
    void orderRide_panicNotActivated() {
        GetRouteResponseDTO routeDTO = createRoute(45.25, 19.85, 45.26, 19.86);

        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(false)
                .route(routeDTO)
                .build();

        mockBaseSuccess();

        when(routeService.routeEstimation(anyString(), anyString(), nullable(String.class)))
                .thenReturn(new RouteQuoteResponseDTO(100.0, 30.0));

        rideService.orderRide(passenger.getEmail(), req);

        ArgumentCaptor<Ride> rideCaptor = ArgumentCaptor.forClass(Ride.class);
        verify(rideRepository).saveAndFlush(rideCaptor.capture());

        Ride savedRide = rideCaptor.getValue();
        assertFalse(savedRide.getPanicActivated());
    }

    @Test
    @DisplayName("Order ride - complaints list is empty")
    void orderRide_complaintsListEmpty() {
        GetRouteResponseDTO routeDTO = createRoute(45.25, 19.85, 45.26, 19.86);

        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(false)
                .route(routeDTO)
                .build();

        mockBaseSuccess();

        when(routeService.routeEstimation(anyString(), anyString(), nullable(String.class)))
                .thenReturn(new RouteQuoteResponseDTO(100.0, 30.0));

        rideService.orderRide(passenger.getEmail(), req);

        ArgumentCaptor<Ride> rideCaptor = ArgumentCaptor.forClass(Ride.class);
        verify(rideRepository).saveAndFlush(rideCaptor.capture());

        Ride savedRide = rideCaptor.getValue();
        assertNotNull(savedRide.getComplaints());
        assertTrue(savedRide.getComplaints().isEmpty());
    }
    @Test
    @DisplayName("Order ride - driver already has scheduled ride in next 15 minutes throws exception")
    void orderRide_driverHasConflictingScheduledRide() {

        GetRouteResponseDTO routeDTO = createRoute(45, 19, 46, 20);

        RideRequestDTO req = RideRequestDTO.builder()
                .vehicleType(VehicleType.STANDARD)
                .babiesAllowed(false)
                .petsAllowed(false)
                .scheduled(false)
                .route(routeDTO)
                .build();

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(passenger));

        when(routeMapper.fromDTO(any()))
                .thenAnswer(invocation -> {
                    GetRouteResponseDTO dto = invocation.getArgument(0);
                    Route r = new Route();
                    r.setStartLocationLat(dto.getStartLocationLat());
                    r.setStartLocationLng(dto.getStartLocationLng());
                    r.setEndLocationLat(dto.getEndLocationLat());
                    r.setEndLocationLng(dto.getEndLocationLng());
                    r.setStops(new ArrayList<>());
                    return r;
                });

        when(routeRepository.save(any(Route.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        when(driverRepository.findFirstAvailableDriver(any(), any(), any(), any(), any()))
                .thenReturn(Optional.of(driver));

        Ride conflictingRide = Ride.builder()
                .startTime(LocalDateTime.now().plusMinutes(5))
                .build();

        when(rideRepository.findScheduledRidesForDriverInTimeRange(any(), any(), any()))
                .thenReturn(List.of(conflictingRide));

        DriverNotAvailableException ex = assertThrows(
                DriverNotAvailableException.class,
                () -> rideService.orderRide(passenger.getEmail(), req)
        );

        assertTrue(ex.getMessage().contains("Driver has a scheduled ride at"));
    }
}