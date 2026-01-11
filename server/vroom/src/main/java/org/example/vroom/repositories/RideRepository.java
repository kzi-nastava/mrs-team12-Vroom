package org.example.vroom.repositories;

import org.example.vroom.entities.Driver;
import org.example.vroom.entities.Ride;
import org.example.vroom.enums.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {


     List<Ride> findByDriverId(Long driverId);
     List<Ride> findByPassengersEmail(String email);
     List<Ride> findByStatus(RideStatus status);
    List<Ride> findByDriverAndStartTimeAfter(Driver driver, LocalDateTime startTime);
}