package org.example.vroom.repositories;

import org.example.vroom.entities.Driver;
import org.example.vroom.entities.DriverProfileUpdateRequest;
import org.example.vroom.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverProfileUpdateRequestRepository
        extends JpaRepository<DriverProfileUpdateRequest, Long> {

    boolean existsByDriverAndStatus(Driver driver, RequestStatus status);
}