package org.example.vroom.services;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.example.vroom.DTOs.requests.LeaveReviewRequestDTO;
import org.example.vroom.DTOs.requests.RideRequestDTO;
import org.example.vroom.DTOs.responses.GetRideResponseDTO;
import org.example.vroom.DTOs.responses.RouteQuoteResponseDTO;
import org.example.vroom.entities.*;
import org.example.vroom.enums.DriverStatus;
import org.example.vroom.enums.RideStatus;
import org.example.vroom.enums.VehicleType;
import org.example.vroom.exceptions.ride.CantReviewRideException;
import org.example.vroom.exceptions.ride.RideNotFoundException;
import org.example.vroom.exceptions.user.NoAvailableDriverException;
import org.example.vroom.exceptions.user.UserNotFoundException;
import org.example.vroom.mappers.RideMapper;
import org.example.vroom.mappers.RouteMapper;
import org.example.vroom.repositories.DriverRepository;
import org.example.vroom.repositories.RideRepository;
import org.example.vroom.repositories.RegisteredUserRepository;
import org.example.vroom.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RideService {

    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private RegisteredUserRepository userRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private RideMapper rideMapper;
    @Autowired
    private RouteMapper routeMapper;
    @Autowired
    private RouteService routeService;
    @Autowired
    private EmailService emailService;

    @Transactional
    public GetRideResponseDTO orderRide(String userEmail, RideRequestDTO request) {

        RegisteredUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (request.getScheduled() && request.getScheduledTime() != null) {
            if (request.getScheduledTime().isAfter(LocalDateTime.now().plusHours(5))) {
                throw new RuntimeException("Scheduled ride cannot be more than 5 hours ahead");
            }
        }

        Route route = routeMapper.fromDTO(request.getRoute());

        // ucitavanje i validacija dodatnih putnika
        List<RegisteredUser> passengers = new ArrayList<>();
        passengers.add(user);
        if (request.getPassengersEmails() != null) {
            for (String email : request.getPassengersEmails()) {
                RegisteredUser p = userRepository.findByEmail(email)
                        .orElseThrow(() -> new UserNotFoundException("Passenger not found: " + email));
                passengers.add(p);
            }
        }

        // odabir dostupnog
        Driver driver = driverRepository.findFirstAvailableDriver(
                request.getVehicleType(),
                request.getBabiesAllowed(),
                request.getPetsAllowed()
        ).orElseThrow(() -> new NoAvailableDriverException("No available drivers"));

        //provera radnog vremena
        if (!driverHasWorkingTime(driver)) {
            throw new NoAvailableDriverException("Driver exceeded 8 working hours in last 24h");
        }


        Ride ride = Ride.builder()
                .passengers(convertToPassengerNames(passengers))
                .driver(driver)
                .route(route)
                .startTime(request.getScheduled() ? request.getScheduledTime() : LocalDateTime.now())
                .status(request.getScheduled() ? RideStatus.ACCEPTED : RideStatus.PENDING)
                .price(calculatePrice(route, request.getVehicleType()))
                .panicActivated(false)
                .build();

        ride = rideRepository.save(ride);

        driver.setStatus(DriverStatus.UNAVAILABLE);

        // slanje notifikacija putem NotificationService
        //notificationService.notifyRideAssigned(user, driver, ride);

        return rideMapper.getRideDTO(ride);
    }
    private ArrayList<String> convertToPassengerNames(List<RegisteredUser> passengers) {
        ArrayList<String> names = new ArrayList<>();
        for (RegisteredUser p : passengers) {
            names.add(p.getFirstName() + " " + p.getLastName());
        }
        return names;
    }

    private boolean driverHasWorkingTime(Driver driver) {

        LocalDateTime since = LocalDateTime.now().minusHours(24);
        List<Ride> rides = rideRepository.findByDriverAndStartTimeAfter(driver, since);

        double totalHours = rides.stream()
                .mapToDouble(r -> {
                    if (r.getEndTime() != null) {
                        return java.time.Duration.between(r.getStartTime(), r.getEndTime()).toMinutes() / 60.0;
                    } else {
                        // ako je ongoing voznja uzmi razliku od pocetka do sad
                        return java.time.Duration.between(r.getStartTime(), LocalDateTime.now()).toMinutes() / 60.0;
                    }
                })
                .sum();

        return totalHours < 8.0;
    }

    private double calculatePrice(Route route, VehicleType vehicleType) {
        // Ako ruta ima stopove, koristi ih, inače prazna lista
        List<Point> stops = route.getStops() != null ? route.getStops() : new ArrayList<>();

        // Napravi string stopLocations za Geoapify API
        String stopLocations = stops.stream()
                .map(stop -> stop.getLat() + "," + stop.getLng())
                .collect(Collectors.joining(";"));

        // Pozovi routeEstimation sa start, end i stopLocations
        RouteQuoteResponseDTO quote = routeService.routeEstimation(
                route.getStartLocationLat() + "," + route.getStartLocationLng(),
                route.getEndLocationLat() + "," + route.getEndLocationLng(),
                stopLocations
        );

        if (quote == null) {
            throw new RuntimeException("Failed to estimate route price");
        }

        // Cena za celu rutu (uključujući sve stopove)
        return quote.getPrice();
    }

    public void leaveReview(Long rideId, LeaveReviewRequestDTO review){
        Optional<Ride> rideOptional = rideRepository.findById(rideId);
        if (rideOptional.isEmpty()) {
            throw new RideNotFoundException("Ride not found");
        }
        Ride ride = rideOptional.get();
        if (ride.getEndTime().plusDays(3).isBefore(LocalDateTime.now())) {
            throw new CantReviewRideException("Can't review because it's been 3 days since the ride ended.");
        }
        ride.setDriverRating(review.getDriverRating());
        ride.setVehicleRating(review.getVehicleRating());
        ride.setComment(review.getComment());

        Driver driver = ride.getDriver();
        driver.setRatingCount(driver.getRatingCount() + 1);
        driver.setRatingSum(driver.getRatingSum() + review.getDriverRating());

        Vehicle vehicle = driver.getVehicle();
        vehicle.setRatingCount(vehicle.getRatingCount() + 1);
        vehicle.setRatingSum(vehicle.getRatingSum() + review.getVehicleRating());

        rideRepository.save(ride);
        driverRepository.save(driver);
        vehicleRepository.save(vehicle);
    }

    public void finishRide(Long RideID){
        Optional<Ride> rideOptional = rideRepository.findById(RideID);
        if (rideOptional.isEmpty()) {
            throw new RideNotFoundException("Ride not found");
        }
        Ride ride = rideOptional.get();
        ride.setEndTime(LocalDateTime.now());
        ride.setStatus(RideStatus.FINISHED);
        Driver driver = ride.getDriver();
        driver.setStatus(DriverStatus.AVAILABLE);
        rideRepository.save(ride);
        driverRepository.save(driver);
        try {
            emailService.sendRideEndMail(ride.getPassenger().getEmail());
        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
        for (String email : ride.getPassengers()) {
            try {
                emailService.sendRideEndMail(email);
            }catch (MessagingException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

