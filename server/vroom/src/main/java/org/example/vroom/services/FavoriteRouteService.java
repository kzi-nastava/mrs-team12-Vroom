package org.example.vroom.services;

import lombok.RequiredArgsConstructor;
import org.example.vroom.DTOs.OrderFromFavoriteRequestDTO;
import org.example.vroom.DTOs.responses.ride.GetRideResponseDTO;
import org.example.vroom.DTOs.responses.route.RouteQuoteResponseDTO;
import org.example.vroom.entities.*;
import org.example.vroom.enums.DriverStatus;
import org.example.vroom.enums.RideStatus;
import org.example.vroom.exceptions.user.NoAvailableDriverException;
import org.example.vroom.mappers.RideMapper;
import org.example.vroom.repositories.DriverRepository;
import org.example.vroom.repositories.FavoriteRouteRepository;
import org.example.vroom.repositories.RegisteredUserRepository;
import org.example.vroom.repositories.RideRepository;
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

        if (route.getStartLocationLat() == null || route.getStartLocationLng() == null ||
                route.getEndLocationLat() == null || route.getEndLocationLng() == null) {
            throw new RuntimeException("Favorite route has invalid start or end coordinates");
        }

        List<RegisteredUser> passengers = new ArrayList<>();
        passengers.add(user);

        Driver driver = driverRepository.findFirstAvailableDriver(
                request.getVehicleType(),
                request.getBabiesAllowed(),
                request.getPetsAllowed()
        ).orElseThrow(() -> new NoAvailableDriverException("No available driver"));

        // --- Provera radnog vremena vozača ---
        if (!driverHasWorkingTime(driver)) {
            throw new RuntimeException("Driver exceeded 8 working hours in last 24h");
        }

        String startLocation = route.getStartLocationLat() + "," + route.getStartLocationLng();
        String endLocation = route.getEndLocationLat() + "," + route.getEndLocationLng();

        String stops = null;
        if (route.getStops() != null && !route.getStops().isEmpty()) {
            stops = route.getStops().stream()
                    .filter(p -> p != null && p.getLat() != null && p.getLng() != null)
                    .map(p -> p.getLat() + "," + p.getLng()) // ovde lat pa lon
                    .collect(Collectors.joining(";"));
        }
        RouteQuoteResponseDTO quote = !StringUtils.hasText(stops)
                ? routeService.routeEstimation(startLocation, endLocation)
                : routeService.routeEstimation(startLocation, endLocation, stops);

        if (quote == null) {
            throw new RuntimeException("Failed to calculate route quote");
        }

        Ride ride = Ride.builder()
                .passenger(user)
                .passengers(convertToPassengerNames(passengers))
                .driver(driver)
                .route(route)
                .startTime(request.getScheduledTime() != null ? request.getScheduledTime() : LocalDateTime.now())
                .status(request.getScheduledTime() != null ? RideStatus.ACCEPTED : RideStatus.PENDING)
                .price(quote.getPrice())
                .panicActivated(false)
                .isScheduled(request.getScheduledTime() != null)
                .build();

        ride = rideRepository.save(ride);

        driver.setStatus(DriverStatus.UNAVAILABLE);

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
                        // ako je ongoing voznja uzmi razliku od pocetka do sad
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
}
