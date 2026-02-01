package org.example.vroom.repositories;

import org.example.vroom.entities.Driver;
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

}