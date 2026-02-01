package org.example.vroom.repositories;
import org.example.vroom.entities.Driver;
import org.example.vroom.enums.DriverStatus;
import org.example.vroom.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Driver> findByStatus(DriverStatus status);

    List<Driver> findByStatusIn(List<DriverStatus> statuses);

    List<Driver> findByVehicleIsNotNull();

    @Query("""
        SELECT d FROM Driver d
        WHERE d.status = :status
        AND d.vehicle IS NOT NULL
    """)
    List<Driver> findAvailableDrivers(@Param("status") DriverStatus status);

    @Query("""
        SELECT d FROM Driver d
        WHERE d.status = 'AVAILABLE'
          AND d.vehicle.type = :vehicleType
          AND (:babiesAllowed IS NULL OR d.vehicle.babiesAllowed = :babiesAllowed)
          AND (:petsAllowed IS NULL OR d.vehicle.petsAllowed = :petsAllowed)
        ORDER BY d.id ASC
        LIMIT 1
    """)
    Optional<Driver> findFirstAvailableDriver(
            @Param("vehicleType") VehicleType vehicleType,
            @Param("babiesAllowed") Boolean babiesAllowed,
            @Param("petsAllowed") Boolean petsAllowed
    );

    @Query("SELECT d.status FROM Driver d WHERE d.id = :id")
    Optional<DriverStatus> findStatusById(@Param("id") Long id);

}
