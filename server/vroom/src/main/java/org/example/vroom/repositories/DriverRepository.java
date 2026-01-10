package org.example.vroom.repositories;
import org.example.vroom.entities.Driver;
import org.example.vroom.enums.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByEmail(String email);

    List<Driver> findByStatus(DriverStatus status);

    List<Driver> findByStatusIn(List<DriverStatus> statuses);

    List<Driver> findByVehicleIsNotNull();

    @Query("""
        SELECT d FROM Driver d
        WHERE d.status = :status
        AND d.vehicle IS NOT NULL
    """)
    List<Driver> findAvailableDrivers(@Param("status") DriverStatus status);
}
