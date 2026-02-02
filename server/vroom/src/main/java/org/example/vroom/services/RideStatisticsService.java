package org.example.vroom.services;

import lombok.RequiredArgsConstructor;
import org.example.vroom.DTOs.responses.ride.DailyRideReportDTO;
import org.example.vroom.DTOs.responses.ride.RideReportDTO;
import org.example.vroom.entities.Ride;
import org.example.vroom.entities.Route;
import org.example.vroom.enums.RideStatus;
import org.example.vroom.repositories.RideRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Service
@RequiredArgsConstructor
public class RideStatisticsService {

    private final RideRepository rideRepository;

    public RideReportDTO passengerReport(
            Long userId,
            LocalDateTime from,
            LocalDateTime to
    ) {
        List<Object[]> rows = rideRepository.passengerStatsRaw(userId, from, to);

        List<Ride> rides = rideRepository
                .findPassengerRidesWithRoute(userId, from, to);

        return buildReportFromRaw(rows, rides);
    }


    public RideReportDTO driverReport(
            Long driverId,
            LocalDateTime from,
            LocalDateTime to
    ) {
        List<Object[]> rows = rideRepository.driverStatsRaw(driverId, from, to);
        List<Ride> rides = rideRepository
                .findDriverRidesWithRoute(driverId, from, to);

        return buildReportFromRaw(rows, rides);
    }

    public RideReportDTO adminReport(
           LocalDateTime from,
           LocalDateTime to
    ) {
        List<Object[]> rows = rideRepository.adminStatsRaw(from, to);
       return buildReportFromRaw(rows);
    }
    private RideReportDTO buildReportFromRaw(List<Object[]> rows) {
        return buildReportFromRaw(rows, List.of());
    }

    private RideReportDTO buildReportFromRaw(
            List<Object[]> rows,
            List<Ride> rides
    ) {
        Map<LocalDate, Double> kmByDate = new HashMap<>();

        double totalKilometers = 0;

        for (Ride ride : rides) {
            LocalDate date = ride.getStartTime().toLocalDate();
            double km = calculateDistance(ride);

            kmByDate.merge(date, km, Double::sum);
            totalKilometers += km;
        }

        List<DailyRideReportDTO> daily = new ArrayList<>();
        long totalRides = 0;
        double totalMoney = 0;

        for (Object[] row : rows) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            long count = ((Number) row[1]).longValue();
            double money = ((Number) row[2]).doubleValue();

            double km = kmByDate.getOrDefault(date, 0.0);

            daily.add(new DailyRideReportDTO(
                    date,
                    count,
                    money,
                    km
            ));

            totalRides += count;
            totalMoney += money;
        }

        daily.sort(Comparator.comparing(DailyRideReportDTO::getDate));

        return new RideReportDTO(
                daily,
                totalRides,
                totalMoney,
                totalKilometers
        );
    }

    private double calculateDistance(Ride ride) {
        Route r = ride.getRoute();
        if (r == null) return 0;

        return distance(
                r.getStartLocationLat(),
                r.getStartLocationLng(),
                r.getEndLocationLat(),
                r.getEndLocationLng()
        );
    }

    public static double distance(
            double lat1, double lon1,
            double lat2, double lon2
    ) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371.0 * c;
    }
}
