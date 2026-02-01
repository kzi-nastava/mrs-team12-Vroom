package org.example.vroom.repositories;

import org.example.vroom.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    boolean existsByLicenceNumber(String licenceNumber);
}
