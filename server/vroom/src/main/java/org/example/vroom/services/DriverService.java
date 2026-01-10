package org.example.vroom.services;

import org.example.vroom.DTOs.DriverDTO;
import org.example.vroom.DTOs.requests.DriverUpdateRequestDTO;
import org.example.vroom.entities.Driver;
import org.example.vroom.enums.DriverStatus;
import org.example.vroom.mappers.DriverMapper;
import org.example.vroom.repositories.DriverRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    public DriverService(DriverRepository driverRepository,
                         DriverMapper driverMapper) {
        this.driverRepository = driverRepository;
        this.driverMapper = driverMapper;
    }

    public DriverDTO getById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        return driverMapper.toDTO(driver);
    }

    public DriverDTO getByEmail(String email) {
        Driver driver = driverRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        return driverMapper.toDTO(driver);
    }

    public List<DriverDTO> getAvailableDrivers() {
        List<Driver> drivers =
                driverRepository.findByStatus(DriverStatus.AVAILABLE);
        return driverMapper.toDTOList(drivers);
    }

    public DriverDTO changeStatus(Long driverId,
                                  DriverUpdateRequestDTO request) {

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        if (driver.getStatus() == DriverStatus.UNAVAILABLE &&
                request.getStatus() == DriverStatus.INACTIVE) {
            throw new RuntimeException("Driver is currently on a ride");
        }

        driver.setStatus(request.getStatus());
        driverRepository.save(driver);

        return driverMapper.toDTO(driver);
    }

    public void addRating(Long driverId, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be 1–5");
        }

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setRatingCount(driver.getRatingCount() + 1);
        driver.setRatingSum(driver.getRatingSum() + rating);

        driverRepository.save(driver);
    }
}
