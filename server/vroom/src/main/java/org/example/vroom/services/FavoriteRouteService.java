package org.example.vroom.services;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.vroom.DTOs.OrderFromFavoriteRequestDTO;
import org.example.vroom.DTOs.requests.ride.CreateFavoriteRouteRequestDTO;
import org.example.vroom.DTOs.requests.ride.FavoriteRouteResponseDTO;
import org.example.vroom.DTOs.responses.ride.GetRideResponseDTO;
import org.example.vroom.DTOs.responses.route.RouteQuoteResponseDTO;
import org.example.vroom.entities.*;
import org.example.vroom.enums.DriverStatus;
import org.example.vroom.enums.RideStatus;
import org.example.vroom.exceptions.user.NoAvailableDriverException;
import org.example.vroom.mappers.FavoriteRouteMapper;
import org.example.vroom.mappers.RideMapper;
import org.example.vroom.repositories.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteRouteService {

    private final FavoriteRouteRepository favoriteRouteRepository;
    private final RegisteredUserRepository registeredUserRepository;
    private final DriverRepository driverRepository;
    private final RideRepository rideRepository;
    private final RideMapper rideMapper;
    private final RouteService routeService;
    private final RouteRepository routeRepository;
    private final FavoriteRouteMapper favoriteRouteMapper;


    public List<FavoriteRoute> getCurrentUserFavorites(String email) {
        RegisteredUser user = registeredUserRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return favoriteRouteRepository.findByUser(user);
    }

    @Transactional
    public GetRideResponseDTO orderFavoriteRoute(String userEmail, OrderFromFavoriteRequestDTO request) {
        RegisteredUser user = registeredUserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FavoriteRoute favorite = favoriteRouteRepository.findById(request.getFavoriteRouteId())
                .orElseThrow(() -> new RuntimeException("Favorite route not found"));

        Route route = favorite.getRoute();
        if (route == null) {
            throw new RuntimeException("Favorite route has no route assigned");
        }
        LocalDateTime scheduledTime = request.getScheduledTime();

        if (scheduledTime != null) {
            LocalDateTime now = LocalDateTime.now();

            if (scheduledTime.isBefore(now)) {
                throw new RuntimeException("Scheduled time cannot be in the past");
            }

            if (scheduledTime.isAfter(now.plusHours(5))) {
                throw new RuntimeException("Scheduled time cannot be more than 5 hours ahead");
            }
        }
        if (route.getStartLocationLat() == null || route.getStartLocationLng() == null ||
                route.getEndLocationLat() == null || route.getEndLocationLng() == null) {
            throw new RuntimeException("Favorite route has invalid start or end coordinates");
        }

        List<RegisteredUser> passengers = new ArrayList<>();
        passengers.add(user);

        Driver driver = driverRepository.findFirstAvailableDriver(
                request.getVehicleType(),
                request.getBabiesAllowed(),
                request.getPetsAllowed(),
                route.getStartLocationLat(),
                route.getStartLocationLng()
        ).orElseThrow(() -> new NoAvailableDriverException("No available drivers"));


        if (!driverHasWorkingTime(driver)) {
            throw new RuntimeException("Driver exceeded 8 working hours in last 24h");
        }

        String startLocation = route.getStartLocationLat() + "," + route.getStartLocationLng();
        String endLocation = route.getEndLocationLat() + "," + route.getEndLocationLng();

        String stops = null;
        if (route.getStops() != null && !route.getStops().isEmpty()) {
            stops = route.getStops().stream()
                    .filter(p -> p != null && p.getLat() != null && p.getLng() != null)
                    .map(p -> p.getLat() + "," + p.getLng())
                    .collect(Collectors.joining(";"));
        }
        RouteQuoteResponseDTO quote = !StringUtils.hasText(stops)
                ? routeService.routeEstimation(startLocation, endLocation)
                : routeService.routeEstimation(startLocation, endLocation, stops);

        if (quote == null) {
            throw new RuntimeException("Failed to calculate route quote");
        }
        Route originalRoute = favorite.getRoute();

        Route newRoute = Route.builder()
                .startAddress(originalRoute.getStartAddress())
                .endAddress(originalRoute.getEndAddress())
                .startLocationLat(originalRoute.getStartLocationLat())
                .startLocationLng(originalRoute.getStartLocationLng())
                .endLocationLat(originalRoute.getEndLocationLat())
                .endLocationLng(originalRoute.getEndLocationLng())
                .build();

        newRoute = routeRepository.save(newRoute);
        Ride ride = Ride.builder()
                .passenger(user)
                .passengers(convertToPassengerNames(passengers))
                .driver(driver)
                .route(newRoute)
                .startTime(request.getScheduledTime() != null ? request.getScheduledTime() : LocalDateTime.now())
                .status(RideStatus.ACCEPTED)
                .price(quote.getPrice())
                .panicActivated(false)
                .isScheduled(request.getScheduledTime() != null)
                .build();

        ride = rideRepository.save(ride);

        if (scheduledTime == null) {
            driver.setStatus(DriverStatus.UNAVAILABLE);
        }

        return rideMapper.getRideDTO(ride);
    }


    private boolean driverHasWorkingTime(Driver driver) {

        LocalDateTime since = LocalDateTime.now().minusHours(24);
        List<Ride> rides = rideRepository.findByDriverAndStartTimeAfter(driver, since);

        double totalHours = rides.stream()
                .mapToDouble(r -> {
                    if (r.getEndTime() != null) {
                        return java.time.Duration.between(r.getStartTime(), r.getEndTime()).toMinutes() / 60.0;
                    } else {
                        return java.time.Duration.between(r.getStartTime(), LocalDateTime.now()).toMinutes() / 60.0;
                    }
                })
                .sum();

        return totalHours < 8.0;
    }
    private ArrayList<String> convertToPassengerNames(List<RegisteredUser> passengers) {
        ArrayList<String> names = new ArrayList<>();
        for (RegisteredUser p : passengers) {
            names.add(p.getFirstName() + " " + p.getLastName());
        }
        return names;
    }

    @Transactional
    public void removeFromFavorites(String userEmail, Long favoriteId) {

        RegisteredUser user = registeredUserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FavoriteRoute favorite = favoriteRouteRepository.findById(favoriteId)
                .orElseThrow(() -> new RuntimeException("Favorite route not found"));

        if (!favorite.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You cannot delete another user's favorite route");
        }

        favoriteRouteRepository.delete(favorite);
    }
  
    public FavoriteRouteResponseDTO createFavoriteFromRide(
            CreateFavoriteRouteRequestDTO request,
            RegisteredUser user) throws NotFoundException {


        Ride ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new NotFoundException("Ride not found"));



        if (favoriteRouteRepository.existsByUserIdAndRouteId(user.getId(), ride.getRoute().getId())) {
            throw new IllegalArgumentException("This route is already in your favorites");
        }


        FavoriteRoute favoriteRoute = FavoriteRoute.builder()
                .user(user)
                .route(ride.getRoute())
                .name(request.getName())
                .startAddress(ride.getRoute().getStartAddress())
                .endAddress(ride.getRoute().getEndAddress())
                .build();

        FavoriteRoute saved = favoriteRouteRepository.save(favoriteRoute);

        return favoriteRouteMapper.toResponseDTO(saved);
    }
}
