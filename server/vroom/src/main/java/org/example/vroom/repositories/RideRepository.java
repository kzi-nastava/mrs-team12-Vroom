package org.example.vroom.repositories;


import org.example.vroom.DTOs.responses.ride.DailyRideReportDTO;
import org.example.vroom.entities.Driver;
import org.example.vroom.entities.RegisteredUser;
import org.example.vroom.entities.Ride;
import org.example.vroom.entities.User;
import org.example.vroom.enums.RideStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RideRepository extends JpaRepository<Ride, Long> {


     List<Ride> findByDriverId(Long driverId);
     //List<Ride> findByPassengerEmail(String email);
     List<Ride> findByStatus(RideStatus status);
    List<Ride> findByDriverAndStartTimeAfter(Driver driver, LocalDateTime startTime);

    @Query("""
        SELECT r
        FROM Ride r
        WHERE r.driver.id = :driverId
          AND (:startDate IS NULL OR r.startTime >= :startDate)
          AND (:endDate IS NULL OR r.startTime <= :endDate)
    """)
    List<Ride> findDriverRideHistory(
            @Param("driverId") Long driverId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Sort sort
    );

    Optional<Ride> findByPassengerEmailAndStatusIn(String email, Collection<RideStatus> statuses);
    Optional<Ride> findByPassengersContainingAndStatusIn(String email, Collection<RideStatus> statuses);

    @Query("""
    SELECT r FROM Ride r
    WHERE r.driver = :driver
      AND r.isScheduled = true
      AND r.startTime BETWEEN :now AND :tenMinutesLater
    """)
    Optional<Ride> findUpcomingScheduledRides(
            @Param("driver") Driver driver,
            @Param("now") LocalDateTime now,
            @Param("tenMinutesLater") LocalDateTime tenMinutesLater
    );

    Optional<Ride> findByDriverAndStatus(Driver driver, RideStatus rideStatus);

    @Query( """
        SELECT r 
        FROM Ride r 
        WHERE r.passenger.id = :userId 
            AND (:startDate IS NULL OR r.startTime >= :startDate)
            AND (:endDate IS NULL OR r.startTime <= :endDate)
    """)
    List<Ride> userRideHistory(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);


    @Query( """
        SELECT r 
        FROM Ride r 
        WHERE (:startDate IS NULL OR r.startTime >= :startDate)
            AND (:endDate IS NULL OR r.startTime <= :endDate)
    """)
    List<Ride> userRideHistory(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    boolean existsByPassengerAndStatusIn(
            RegisteredUser passenger,
            List<RideStatus> statuses

    @Query("""
    SELECT CAST(r.startTime AS date), COUNT(r), COALESCE(SUM(r.price), 0)
    FROM Ride r
    WHERE
        r.passenger.id = :userId
        AND r.status = org.example.vroom.enums.RideStatus.FINISHED
        AND r.startTime BETWEEN :from AND :to
    GROUP BY CAST(r.startTime AS date)
    ORDER BY CAST(r.startTime AS date)
""")

    List<Object[]> passengerStatsRaw(
            @Param("userId") Long userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
SELECT CAST(r.startTime AS date), COUNT(r), COALESCE(SUM(r.price), 0)
FROM Ride r
WHERE
    r.driver.id = :driverId
    AND r.status = org.example.vroom.enums.RideStatus.FINISHED
    AND r.startTime BETWEEN :from AND :to
GROUP BY CAST(r.startTime AS date)
ORDER BY CAST(r.startTime AS date)
""")
    List<Object[]> driverStatsRaw(
            @Param("driverId") Long driverId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
    SELECT FUNCTION('DATE', r.startTime), COUNT(r), COALESCE(SUM(r.price), 0)
    FROM Ride r
    WHERE
        r.status = org.example.vroom.enums.RideStatus.FINISHED
        AND r.startTime BETWEEN :from AND :to
    GROUP BY FUNCTION('DATE', r.startTime)
    ORDER BY FUNCTION('DATE', r.startTime)
""")
    List<Object[]> adminStatsRaw(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );


    @Query("""
    SELECT r FROM Ride r
    JOIN FETCH r.route
    WHERE r.passenger.id = :userId
      AND r.status = org.example.vroom.enums.RideStatus.FINISHED
      AND r.startTime BETWEEN :from AND :to
""")
    List<Ride> findPassengerRidesWithRoute(
            @Param("userId") Long userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
SELECT r
FROM Ride r
JOIN FETCH r.route rt
WHERE
    r.driver.id = :driverId
    AND r.status = org.example.vroom.enums.RideStatus.FINISHED
    AND r.startTime BETWEEN :from AND :to
""")
    List<Ride> findDriverRidesWithRoute(
            @Param("driverId") Long driverId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    // Svi korisnici sa detaljima ruta
    @Query("""
SELECT r
FROM Ride r
JOIN FETCH r.route
WHERE r.passenger IS NOT NULL
  AND r.status = org.example.vroom.enums.RideStatus.FINISHED
  AND r.startTime BETWEEN :from AND :to
""")
    List<Ride> findAllUsersRidesWithRoute(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    // Svi vozači sa detaljima ruta
    @Query("""
SELECT r
FROM Ride r
JOIN FETCH r.route
WHERE r.driver IS NOT NULL
  AND r.status = org.example.vroom.enums.RideStatus.FINISHED
  AND r.startTime BETWEEN :from AND :to
""")
    List<Ride> findAllDriversRidesWithRoute(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    // Statistik za sve korisnike (po datumu)
    @Query("""
    SELECT CAST(r.startTime AS date), COUNT(r), COALESCE(SUM(r.price), 0)
    FROM Ride r
    WHERE r.passenger IS NOT NULL
      AND r.status = org.example.vroom.enums.RideStatus.FINISHED
      AND r.startTime BETWEEN :from AND :to
    GROUP BY CAST(r.startTime AS date)
    ORDER BY CAST(r.startTime AS date)
""")
    List<Object[]> adminAllUsersStatsRaw(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    // Statistik za sve vozače (po datumu)
    @Query("""
    SELECT CAST(r.startTime AS date), COUNT(r), COALESCE(SUM(r.price), 0)
    FROM Ride r
    WHERE r.driver IS NOT NULL
      AND r.status = org.example.vroom.enums.RideStatus.FINISHED
      AND r.startTime BETWEEN :from AND :to
    GROUP BY CAST(r.startTime AS date)
    ORDER BY CAST(r.startTime AS date)
""")
    List<Object[]> adminAllDriversStatsRaw(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}