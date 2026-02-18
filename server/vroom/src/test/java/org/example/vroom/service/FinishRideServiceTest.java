package org.example.vroom.service;

import jakarta.mail.MessagingException;
import org.example.vroom.entities.Driver;
import org.example.vroom.entities.RegisteredUser;
import org.example.vroom.entities.Ride;
import org.example.vroom.enums.DriverStatus;
import org.example.vroom.enums.RideStatus;
import org.example.vroom.repositories.DriverRepository;
import org.example.vroom.repositories.RideRepository;
import org.example.vroom.utils.EmailService;
import org.example.vroom.services.RideService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FinishRideServiceTest {
    @Mock
    private RideRepository rideRepository;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private RideService rideService;

    private Ride ride;
    private Driver driver;
    private RegisteredUser passenger;

    @BeforeEach
    void setUp() {
        driver = new Driver();
        driver.setStatus(DriverStatus.UNAVAILABLE);

        passenger = new RegisteredUser();
        passenger.setEmail("email@example.com");

        ride = new Ride();
        ride.setId(1L);
        ride.setDriver(driver);
        ride.setPassenger(passenger);
        ride.setPassengers(List.of("passenger1@example.com", "passenger2@example.com"));
        ride.setStatus(RideStatus.ONGOING);
    }

    @Test
    @DisplayName("Correct Data - Existing Ride - Changes persist")
    void testFinishRideSuccess() throws MessagingException, IOException {
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

        rideService.finishRide(1L);

        assertEquals(RideStatus.FINISHED, ride.getStatus());
        assertNotNull(ride.getEndTime());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());

        verify(rideRepository).save(ride);
        verify(driverRepository).save(driver);
        verify(emailService, times(3)).sendRideEndMail(anyString());
        verify(emailService).sendRideEndMail("email@example.com");
        verify(emailService).sendRideEndMail("passenger1@example.com");
        verify(emailService).sendRideEndMail("passenger2@example.com");
    }

    @Test
    @DisplayName("Invalid Ride ID sent - should throw exception")
    void testFinishRideNotFound() throws MessagingException, IOException {
        when(rideRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> rideService.finishRide(999L));

        verify(rideRepository, never()).save(any());
        verify(driverRepository, never()).save(any());
        verify(emailService, never()).sendRideEndMail(anyString());
    }

    @Test
    @DisplayName("No Linked passengers - shouldn't break while sending emails")
    void testFinishRideWithNoLinkedPassengers() throws MessagingException, IOException {
        ride.setPassengers(Collections.emptyList());
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

        rideService.finishRide(1L);

        assertEquals(RideStatus.FINISHED, ride.getStatus());
        assertNotNull(ride.getEndTime());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());

        verify(rideRepository).save(ride);
        verify(driverRepository).save(driver);
        verify(emailService, times(1)).sendRideEndMail(anyString());
        verify(emailService).sendRideEndMail("email@example.com");
    }

    @Test
    @DisplayName("Passenger List is null - should skip sending emails")
    void testFinishRideWithNullPassengersList() throws MessagingException, IOException {
        ride.setPassengers(null);
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

        rideService.finishRide(1L);

        assertEquals(RideStatus.FINISHED, ride.getStatus());
        assertNotNull(ride.getEndTime());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());

        verify(rideRepository).save(ride);
        verify(driverRepository).save(driver);
        verify(emailService, times(1)).sendRideEndMail(anyString());
        verify(emailService).sendRideEndMail("email@example.com");
    }

    @Test
    @DisplayName("Driver is null - should throw null pointer exception")
    void testFinishRideWithNullDriver() {
        ride.setDriver(null);
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));

        assertThrows(NullPointerException.class, () -> rideService.finishRide(1L));
        verify(driverRepository, never()).save(any());
    }
}