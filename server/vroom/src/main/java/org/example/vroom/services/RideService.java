package org.example.vroom.services;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.example.vroom.DTOs.requests.ride.*;
import org.example.vroom.DTOs.RideDTO;
import org.example.vroom.DTOs.requests.ride.CancelRideRequestDTO;
import org.example.vroom.DTOs.requests.ride.LeaveReviewRequestDTO;
import org.example.vroom.DTOs.requests.ride.RideRequestDTO;
import org.example.vroom.DTOs.requests.ride.StopRideRequestDTO;
import org.example.vroom.DTOs.responses.ride.GetRideResponseDTO;
import org.example.vroom.DTOs.responses.ride.StoppedRideResponseDTO;
import org.example.vroom.DTOs.responses.route.GetRouteResponseDTO;
import org.example.vroom.DTOs.responses.route.RouteQuoteResponseDTO;
import org.example.vroom.entities.*;
import org.example.vroom.enums.DriverStatus;
import org.example.vroom.enums.RideStatus;
import org.example.vroom.enums.VehicleType;
import org.example.vroom.exceptions.ride.*;
import org.example.vroom.exceptions.user.NoAvailableDriverException;
import org.example.vroom.exceptions.user.UserNotFoundException;
import org.example.vroom.mappers.RideMapper;
import org.example.vroom.mappers.RouteMapper;
import org.example.vroom.repositories.*;
import org.example.vroom.utils.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
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

    @Autowired
    private FavoriteRouteRepository favoriteRouteRepository;


    public Ride orderFromFavorite(
            OrderFromFavoriteRequestDTO request,
            String userEmail
    ) {
        RegisteredUser user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));


        FavoriteRoute favorite = favoriteRouteRepository
                .findById(request.getFavoriteRouteId())
                .orElseThrow(() -> new RuntimeException("Favorite route not found"));

        Route routeCopy = copyRoute(favorite.getRoute());

        Ride ride = Ride.builder()
                .passenger(user)
                .route(routeCopy)
                .status(RideStatus.PENDING)
                .isScheduled(Boolean.FALSE)
                .panicActivated(false)
                .build();

        return rideRepository.save(ride);
    }

    private Route copyRoute(Route original) {
        Route route = new Route();
        route.setStartLocationLat(original.getStartLocationLat());
        route.setStartLocationLng(original.getStartLocationLng());
        route.setEndLocationLat(original.getEndLocationLat());
        route.setEndLocationLng(original.getEndLocationLng());

        if (original.getStops() != null) {
            List<Point> stops = original.getStops().stream()
                    .map(p -> {
                        Point np = new Point();
                        np.setLat(p.getLat());
                        np.setLng(p.getLng());
                        return np;
                    })
                    .toList();
            route.setStops(stops);
        }

        return route;
    }

    public GetRouteResponseDTO getRoute(Long rideID){
        Optional<Ride> rideOptional = this.rideRepository.findById(rideID);
        if (rideOptional.isPresent()) {
            Ride ride = rideOptional.get();
            return routeMapper.getRouteDTO(ride.getRoute());
        }
        return null;
    }



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

                if (email == null || email.isBlank()) {
                    continue;   // preskacem praznei null mejlove
                }

                RegisteredUser p = userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new UserNotFoundException("Passenger not found: " + email));

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
                .passenger(user)
                .passengers(convertToPassengerNames(passengers))
                .driver(driver)
                .route(route)
                .startTime(request.getScheduled() ? request.getScheduledTime() : LocalDateTime.now())
                .status(request.getScheduled() ? RideStatus.ACCEPTED : RideStatus.PENDING)
                .price(calculatePrice(route, request.getVehicleType()))
                .panicActivated(false)
                .isScheduled(request.getScheduled() != null && request.getScheduled())
                .build();

        ride = rideRepository.save(ride);

        driver.setStatus(DriverStatus.UNAVAILABLE);

        // slanje notifikacija putem NotificationService
        //notificationService.notifyRideAssigned(user, driver, ride);

        return rideMapper.getRideDTO(ride);
    }

    public Ride getActiveRideForDriver(String driverEmail) {

        driverEmail = "lazarvilotic87@gmail.com";
        System.out.println("Driver email u metodi: " + driverEmail);

        Optional<Driver> driverOpt = driverRepository.findByEmail(driverEmail);

        if (driverOpt.isEmpty()) {
            System.out.println("Nije pronađen driver za email: " + driverEmail);
        } else {
            Driver driver = driverOpt.get();
            System.out.println("Pronađen driver: " + driver.getFirstName() + " " + driver.getLastName());
        }

        Driver driver = driverOpt.orElseThrow(() -> new UserNotFoundException("Driver not found"));

        Optional<Ride> rideOpt = rideRepository.findByDriverAndStatus(driver, RideStatus.ACCEPTED);

        if (rideOpt.isEmpty()) {
            System.out.println("Nema aktivne vožnje za drivera: " + driverEmail);
        } else {
            System.out.println("Pronađena aktivna vožnja: rideId=" + rideOpt.get().getId());
        }

        return rideOpt.orElse(null);
    }

    public GetRideResponseDTO mapToDTO(Ride ride) {
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
        List<Point> stops = route.getStops();
        if (stops == null) {
            stops = new ArrayList<>();
        }

        String stopLocations = stops.isEmpty()
                ? null
                : stops.stream()
                .map(stop -> stop.getLat() + "," + stop.getLng())
                .collect(Collectors.joining(";"));

        RouteQuoteResponseDTO quote = routeService.routeEstimation(
                route.getStartLocationLat() + "," + route.getStartLocationLng(),
                route.getEndLocationLat() + "," + route.getEndLocationLng(),
                stopLocations
        );

        if (quote == null) {
            throw new RuntimeException("Failed to estimate route price");
        }

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

    public void sendComplaint(Long rideID, ComplaintRequestDTO complaint){
        if (complaint == null) {
            throw new EmptyBodyException("Complaint is empty");
        }
        Optional<Ride> rideOptional = rideRepository.findById(rideID);
        if (rideOptional.isEmpty()) {
            throw new RideNotFoundException("Ride not found");
        }
        Ride ride = rideOptional.get();
        ride.getComplaints().add(complaint.getComplaintBody());
        rideRepository.save(ride);
    }

    public void cancelRide(Long rideID, CancelRideRequestDTO data){
        Optional<Ride> rideOptional = rideRepository.findById(rideID);
        if(rideOptional.isEmpty())
            throw new RideNotFoundException("Ride not found");

        Ride ride = rideOptional.get();
        if(!ride.getStatus().equals(RideStatus.ACCEPTED))
            throw new RideCancellationException("Ride hasn't been accepted or it is finished");

        String userType = data.getType();

        if("REGISTERED_USER".equals(userType)){
            if(ride.getStartTime().minusMinutes(10).isBefore(LocalDateTime.now()))
                throw new RideCancellationException("Passengers cannot cancel ride less than 10 minutes before it starts");

            ride.setStatus(RideStatus.CANCELLED_BY_USER);
        }
        else if("DRIVER".equals(userType)){
            if(data.getReason() == null || data.getReason().isBlank()){
                throw new RideCancellationException("Driver must provide a reason for cancellation");
            }

            ride.setStatus(RideStatus.CANCELLED_BY_DRIVER);
            ride.setCancelReason(data.getReason());
        }

        rideRepository.save(ride);
    }

    @Transactional
    public StoppedRideResponseDTO stopRide(Long rideID, StopRideRequestDTO data){
        Optional<Ride> rideOptional = rideRepository.findById(rideID);
        if(rideOptional.isEmpty())
            throw new RideNotFoundException("Ride not found");

        Ride ride = rideOptional.get();
        if(!ride.getStatus().equals(RideStatus.ONGOING))
            throw new StopRideException("Ride must be active in order to stop it");

        Route route = ride.getRoute();

        route.setEndLocationLat(data.getStopLat());
        route.setEndLocationLng(data.getStopLng());
        route.getStops().clear();

        ride.setEndTime(data.getEndTime());
        ride.setRoute(route);
        ride.setStatus(RideStatus.FINISHED);
        double price = this.calculatePrice(ride.getRoute(), ride.getDriver().getVehicle().getType());

        ride.setPrice(price);
        rideRepository.save(ride);

        return rideMapper.stopRide(ride, data, price);
    }

    public GetRideResponseDTO startRide(Long rideId) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride not found"));

        if (!ride.getStatus().equals(RideStatus.ACCEPTED)) {
            throw new RuntimeException("Ride must be ACCEPTED to start");
        }

        ride.setStartTime(LocalDateTime.now());
        ride.setStatus(RideStatus.ONGOING);

        ride = rideRepository.save(ride);

        return rideMapper.getRideDTO(ride);
    }

}

