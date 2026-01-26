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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private GeoService geoService;


    @Autowired
    private FavoriteRouteRepository favoriteRouteRepository;

    private static final Logger log = LoggerFactory.getLogger(RideService.class);


    @Transactional
    public GetRideResponseDTO orderRide(String userEmail, RideRequestDTO request) throws IOException {
        RegisteredUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Validacija zakazanog vremena
        LocalDateTime scheduledTime = null;
        boolean isScheduled = request.getScheduled() != null && request.getScheduled() && request.getScheduledTime() != null;

        if (isScheduled) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime max = now.plusHours(5);

            if (request.getScheduledTime().isBefore(now)) {
                throw new RuntimeException("Scheduled time cannot be in the past");
            }

            if (request.getScheduledTime().isAfter(max)) {
                throw new RuntimeException("Scheduled ride cannot be more than 5 hours ahead");
            }

            scheduledTime = request.getScheduledTime();
        }

        // Konvertovanje rute iz DTO
        Route route = routeMapper.fromDTO(request.getRoute());

        // Putnici
        List<RegisteredUser> passengers = new ArrayList<>();
        passengers.add(user);
        if (request.getPassengersEmails() != null) {
            for (String email : request.getPassengersEmails()) {
                if (email == null || email.isBlank()) continue;
                RegisteredUser p = userRepository.findByEmail(email)
                        .orElseThrow(() -> new UserNotFoundException("Passenger not found: " + email));
                passengers.add(p);
            }
        }

        // Driver
        Driver driver = driverRepository.findFirstAvailableDriver(
                request.getVehicleType(),
                request.getBabiesAllowed(),
                request.getPetsAllowed()
        ).orElseThrow(() -> new NoAvailableDriverException("No available drivers"));

        if (!driverHasWorkingTime(driver)) {
            throw new NoAvailableDriverException("Driver exceeded 8 working hours in last 24h");
        }
        String startAddress = geoService.reverseGeocode(
                route.getStartLocationLat(),
                route.getStartLocationLng()
        );

        String endAddress = geoService.reverseGeocode(
                route.getEndLocationLat(),
                route.getEndLocationLng()
        );

        route.setStartAddress(startAddress);
        route.setEndAddress(endAddress);
        // Start / End
        String startLocation = route.getStartLocationLat() + "," + route.getStartLocationLng();
        String endLocation = route.getEndLocationLat() + "," + route.getEndLocationLng();

        String stops = null;
        if (route.getStops() != null && !route.getStops().isEmpty()) {
            stops = route.getStops().stream()
                    .filter(p -> p != null && p.getLat() != null && p.getLng() != null)
                    .map(p -> p.getLat() + "," + p.getLng())
                    .collect(Collectors.joining(";"));
        }

        RouteQuoteResponseDTO quote = routeService.routeEstimation(startLocation, endLocation, stops);

        // StartTime = scheduledTime ako je zakazano
        LocalDateTime startTime = isScheduled ? scheduledTime : LocalDateTime.now();
        RideStatus status = isScheduled ? RideStatus.ACCEPTED : RideStatus.PENDING;

        Ride ride = Ride.builder()
                .passenger(user)
                .passengers(convertToPassengerNames(passengers))
                .driver(driver)
                .route(route)
                .startTime(startTime)
                .status(status)
                .price(quote.getPrice())
                .panicActivated(false)
                .isScheduled(isScheduled)
                .build();

        ride = rideRepository.save(ride);
        if (!isScheduled) {
            driver.setStatus(DriverStatus.UNAVAILABLE);
        }
        // DTO
        GetRideResponseDTO dto = rideMapper.getRideDTO(ride);
        dto.setScheduledTime(scheduledTime);

        return dto;
    }



    public Ride getActiveRideForDriver(String driverEmail) {
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
            if(ride.getStartTime() != null && ride.getStartTime().minusMinutes(10).isBefore(LocalDateTime.now()))
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
    public void calculatePrice(){}

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
        //double price = this.calculatePrice();
        double price=1;

        ride.setPrice(price);
        rideRepository.save(ride);

        return rideMapper.stopRide(ride, data, price);
    }

    public GetRideResponseDTO startRide(String driverEmail) {

        Driver driver = driverRepository.findByEmail(driverEmail)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        Ride ride = rideRepository.findByDriverAndStatus(driver, RideStatus.ACCEPTED)
                .orElseThrow(() -> new RideNotFoundException("No accepted ride for driver"));

        ride.setStartTime(LocalDateTime.now());
        ride.setStatus(RideStatus.ONGOING);

        rideRepository.save(ride);

        return rideMapper.getRideDTO(ride);
    }


}

