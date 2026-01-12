package org.example.vroom.services;

import ch.qos.logback.classic.encoder.JsonEncoder;
import org.example.vroom.DTOs.DriverDTO;
import org.example.vroom.DTOs.requests.DriverRegistrationRequestDTO;
import org.example.vroom.DTOs.requests.DriverUpdateRequestDTO;
import org.example.vroom.entities.Driver;
import org.example.vroom.enums.DriverStatus;
import org.example.vroom.exceptions.user.DriverAlreadyExistsException;
import org.example.vroom.exceptions.user.DriverNotFoundException;
import org.example.vroom.exceptions.user.DriverStatusChangeNotAllowedException;
import org.example.vroom.mappers.DriverMapper;
import org.example.vroom.mappers.DriverProfileMapper;
import org.example.vroom.repositories.DriverRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final PasswordEncoder passwordEncoder;
    private final DriverProfileMapper driverProfileMapper;

    public DriverService(DriverRepository driverRepository,
                         DriverMapper driverMapper, PasswordEncoder passwordEncoder, DriverProfileMapper driverProfileMapper) {
        this.driverRepository = driverRepository;
        this.driverMapper = driverMapper;
        this.passwordEncoder = passwordEncoder;
        this.driverProfileMapper = driverProfileMapper;
    }

    public DriverDTO getById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));
        return driverMapper.toDTO(driver);
    }

    public DriverDTO getByEmail(String email) {
        Driver driver = driverRepository.findByEmail(email)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));
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
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));

        if (driver.getStatus() == DriverStatus.UNAVAILABLE &&
                request.getStatus() == DriverStatus.INACTIVE) {
            throw new DriverStatusChangeNotAllowedException("Driver is currently on a ride");
        }

        driver.setStatus(request.getStatus());
        driverRepository.save(driver);

        return driverMapper.toDTO(driver);
    }
    @Transactional
    public DriverDTO registerDriver(DriverRegistrationRequestDTO request) {

        if (driverRepository.existsByEmail(request.getEmail())) {
            throw new DriverAlreadyExistsException(
                    "Driver with this email already exists"
            );
        }

        Driver driver = driverMapper.toEntity(request);
        driver.setPassword(passwordEncoder.encode(request.getPassword()));
        driver.setStatus(DriverStatus.UNAVAILABLE);
        driver.setBlockedReason(null);

        Driver saved = driverRepository.saveAndFlush(driver);

        return driverMapper.toDTO(saved);
    }

    public DriverDTO getMyProfile(String email) {

        Driver driver = driverRepository.findByEmail(email)
                .orElseThrow(() ->
                        new DriverNotFoundException("Driver not found"));

        return driverProfileMapper.toDTO(driver);
    }

    @Transactional
    public DriverDTO updateMyProfile(String email, DriverDTO dto) {

        Driver driver = driverRepository.findByEmail(email)
                .orElseThrow(() ->
                        new DriverNotFoundException("Driver not found"));

        driverProfileMapper.updateEntity(driver, dto);

        return driverProfileMapper.toDTO(
                driverRepository.save(driver)
        );
    }

}
