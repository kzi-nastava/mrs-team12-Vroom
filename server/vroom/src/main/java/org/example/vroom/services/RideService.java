package org.example.vroom.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.example.vroom.DTOs.requests.ride.*;
import org.example.vroom.DTOs.RideDTO;
import org.example.vroom.DTOs.responses.ride.GetActiveRideInfoDTO;
import org.example.vroom.DTOs.responses.ride.GetRideResponseDTO;
import org.example.vroom.DTOs.responses.ride.StoppedRideResponseDTO;
import org.example.vroom.DTOs.responses.route.GetRouteResponseDTO;
import org.example.vroom.DTOs.responses.route.PointResponseDTO;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
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
    private RouteRepository routeRepository;


    @Autowired
    private FavoriteRouteRepository favoriteRouteRepository;

    private static final Logger log = LoggerFactory.getLogger(RideService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Transactional
    public GetRideResponseDTO orderRide(String userEmail, RideRequestDTO request) {
        RegisteredUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

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


        Route route = routeMapper.fromDTO(request.getRoute());
        route = routeRepository.save(route);

        Set<String> uniquePassengers = new LinkedHashSet<>();
        if (request.getPassengersEmails() != null) {
            for (String email : request.getPassengersEmails()) {
                if (email != null && !email.isBlank()) {
                    uniquePassengers.add(email);
                }
            }
        }
        List<String> passengers = new ArrayList<>(uniquePassengers);
        List<String> complaints = new ArrayList<>();

        Driver driver = driverRepository.findFirstAvailableDriver(
                request.getVehicleType(),
                request.getBabiesAllowed(),
                request.getPetsAllowed()
        ).orElseThrow(() -> new NoAvailableDriverException("No available drivers"));
        int vehicleCapacity = driver.getVehicle().getNumberOfSeats();
        int totalPassengers = 1 + passengers.size();

        if (totalPassengers > vehicleCapacity) {
            throw new TooManyPassengersException(
                    "Vehicle capacity exceeded: max " + vehicleCapacity + ", requested " + totalPassengers
            );
        }
//        if (!driverHasWorkingTime(driver)) {
//            throw new NoAvailableDriverException("Driver exceeded 8 working hours in last 24h");
//        }
//        String startAddress = geoService.reverseGeocode(
//                route.getStartLocationLat(),
//                route.getStartLocationLng()
//        );
//
//        String endAddress = geoService.reverseGeocode(
//                route.getEndLocationLat(),
//                route.getEndLocationLng()
//        );
//
//        route.setStartAddress(startAddress);
//        route.setEndAddress(endAddress);
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

        RideStatus status = RideStatus.ACCEPTED;
        driver.setStatus(DriverStatus.UNAVAILABLE);
        if (route.getStartLocationLat().equals(route.getEndLocationLat()) &&
                route.getStartLocationLng().equals(route.getEndLocationLng())) {
            throw new IllegalArgumentException("Start and end locations cannot be the same");
        }
        Ride ride = Ride.builder()
                .passenger(user)
                .passengers(passengers)
                .driver(driver)
                .route(route)
                .complaints(complaints)
                .status(status)
                .price(quote.getPrice())
                .panicActivated(false)
                .isScheduled(isScheduled)
                .build();

        String startAddress = decodeAddress(ride.getRoute().getStartLocationLat(), ride.getRoute().getStartLocationLng());
        String endAddress = decodeAddress(ride.getRoute().getEndLocationLat(), ride.getRoute().getEndLocationLng());
        ride.getRoute().setStartAddress(startAddress);
        ride.getRoute().setEndAddress(endAddress);

        ride = rideRepository.save(ride);

        GetRideResponseDTO dto = rideMapper.getRideDTO(ride);
        dto.setScheduledTime(scheduledTime);

        return dto;
    }

    public GetActiveRideInfoDTO getActiveRideInfo(Long rideId) {
        Optional<Ride> rideOpt = rideRepository.findById(rideId);
        if (rideOpt.isPresent()) {
            Ride ride = rideOpt.get();
            GetActiveRideInfoDTO dto = rideMapper.getActiveRideInfo(ride);
            return dto;
        }
        throw new RideNotFoundException("Ride not found");
    }

    public List<GetRideResponseDTO> getAllActiveRides() {
        List<Ride> rides = rideRepository.findByStatus(RideStatus.ONGOING);
        List<GetRideResponseDTO> dtos = new ArrayList<>();
        for (Ride ride : rides) {
            rideMapper.getRideDTO(ride);
            dtos.add(rideMapper.getRideDTO(ride));
        }
        return dtos;
    }

    public GetRideResponseDTO getUserRide(String userEmail) {
        // Check if they are the person
        Collection<RideStatus> statuses = new ArrayList<>();
        statuses.add(RideStatus.ACCEPTED);
        statuses.add(RideStatus.ONGOING);
        Optional<Ride> creatorRide = this.rideRepository.findByPassengerEmailAndStatusIn(userEmail, statuses);
        if (creatorRide.isPresent()) {
            Ride ride = creatorRide.get();
            return rideMapper.getRideDTO(ride);
        }
        Optional<Ride> passengerRide = this.rideRepository.findByPassengersContainingAndStatusIn(userEmail, statuses);
        if (passengerRide.isPresent()) {
            Ride ride = passengerRide.get();
            return rideMapper.getRideDTO(ride);
        }
        return null;
    }

    public RideStatus getRideStatus(String rideId) {
        Optional<Ride> rideOpt = this.rideRepository.findById(Long.valueOf(rideId));
        if (rideOpt.isEmpty()) {
            throw new RideNotFoundException("Ride not found");
        }
        return rideOpt.get().getStatus();
    }

    private String decodeAddress(Double lat, Double lng) {
        String url = String.format(Locale.US, "https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f", lat, lng);
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Vroom (korda.isidora@gmail.com)");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String rawResponse = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
            JsonNode response = objectMapper.readTree(rawResponse);

            if (response != null && response.has("display_name")) {
                String fullAddress = response.get("display_name").asText();
                String[] parts = fullAddress.split(",");

                if (parts.length >= 2){
                    return parts[1].trim() + " " + parts[0].trim();
                }
                return parts[0].trim();
            }
        }catch (Exception e){
            e.printStackTrace();
            return "Unknown address";
        }
        return "Unknown address";
    }

    public GetRouteResponseDTO getRoute(Long rideID){
        Optional<Ride> rideOptional = this.rideRepository.findById(rideID);
        if (rideOptional.isPresent()) {
            Ride ride = rideOptional.get();
            return routeMapper.getRouteDTO(ride.getRoute());
        }
        return null;
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
        if (ride.getComplaints().isEmpty()){
            ride.setComplaints(new ArrayList<>());
        }
        ride.getComplaints().add(complaint.getComplaintBody());
        rideRepository.save(ride);
    }

    public void cancelRide(Long rideID, String reason, String userType){
        Optional<Ride> rideOptional = rideRepository.findById(rideID);
        if(rideOptional.isEmpty())
            throw new RideNotFoundException("Ride not found");

        Ride ride = rideOptional.get();
        if(!ride.getStatus().equals(RideStatus.ACCEPTED))
            throw new RideCancellationException("Ride hasn't been accepted or it is finished");

        if("REGISTERED_USER".equals(userType)){
            if(ride.getStartTime() != null && ride.getStartTime().minusMinutes(10).isBefore(LocalDateTime.now()))
                throw new RideCancellationException("Passengers cannot cancel ride less than 10 minutes before it starts");

            ride.setStatus(RideStatus.CANCELLED_BY_USER);
        }
        else if("DRIVER".equals(userType)){
            if(reason == null || reason.isEmpty() || reason.isBlank()){
                throw new RideCancellationException("Driver must provide a reason for cancellation");
            }

            ride.setStatus(RideStatus.CANCELLED_BY_DRIVER);
            ride.setCancelReason(reason);
        }

        if(ride.getDriver() != null){
            Driver driver = ride.getDriver();
            driver.setStatus(DriverStatus.AVAILABLE);

            driverRepository.save(driver);
        }

        rideRepository.save(ride);
    }

    public double calculatePrice(String startLocation, String endLocation){
        RouteQuoteResponseDTO data = routeService.routeEstimation(startLocation, endLocation);

        return data.getPrice();
    }

    public StoppedRideResponseDTO stopRide(Long rideID, StopRideRequestDTO data){
        if (data.getStopLat() < -90 || data.getStopLat() > 90 || data.getStopLng() < -180 || data.getStopLng() > 180) {
            throw new StopRideException("Invalid coordinates");
        }

        Optional<Ride> rideOptional = rideRepository.findById(rideID);
        if(rideOptional.isEmpty())
            throw new RideNotFoundException("Ride not found");

        Ride ride = rideOptional.get();
        if(!ride.getStatus().equals(RideStatus.ONGOING))
            throw new StopRideException("Ride must be active in order to stop it");

        Route route = ride.getRoute();

        route.setEndLocationLat(data.getStopLat());
        route.setEndLocationLng(data.getStopLng());

        String endAddress = decodeAddress(data.getStopLat(), data.getStopLng());
        route.setEndAddress(endAddress);
        route.getStops().clear();

        ride.setEndTime(data.getEndTime());
        ride.setRoute(route);
        ride.setStatus(RideStatus.FINISHED);

        String startAddress = String.valueOf(route.getStartLocationLat())+","+String.valueOf(route.getStartLocationLng());
        String stopAddress = String.valueOf(data.getStopLat())+","+String.valueOf(data.getStopLng());
        double price = this.calculatePrice(startAddress, stopAddress);

        if (ride.getDriver() != null) {
            Driver driver = ride.getDriver();
            driver.setStatus(DriverStatus.AVAILABLE);

            driverRepository.save(driver);
        }
        ride.setPrice(price);
        rideRepository.save(ride);

        return rideMapper.stopRide(ride, data, price, endAddress);
    }


    public GetRideResponseDTO startRide(Long rideID) {

        Optional<Ride> rideOpt = rideRepository.findById(rideID);
        if (rideOpt.isEmpty()) {
            throw new RideNotFoundException("Ride not found");
        }
        Ride ride = rideOpt.get();
        ride.setStartTime(LocalDateTime.now());
        ride.setStatus(RideStatus.ONGOING);
        ride.getDriver().setStatus(DriverStatus.UNAVAILABLE);
        rideRepository.save(ride);

        return rideMapper.getRideDTO(ride);
    }

}

