package org.example.vroom.services;

import org.example.vroom.DTOs.DriverDTO;
import org.example.vroom.DTOs.requests.driver.DriverRegistrationRequestDTO;
import org.example.vroom.DTOs.requests.driver.DriverUpdateRequestDTO;
import org.example.vroom.DTOs.responses.ride.RideHistoryResponseDTO;
import org.example.vroom.entities.Driver;
import org.example.vroom.entities.Ride;
import org.example.vroom.enums.DriverStatus;
import org.example.vroom.exceptions.user.DriverAlreadyExistsException;
import org.example.vroom.exceptions.user.DriverNotFoundException;
import org.example.vroom.exceptions.user.DriverStatusChangeNotAllowedException;
import org.example.vroom.mappers.DriverMapper;
import org.example.vroom.mappers.DriverProfileMapper;
import org.example.vroom.mappers.RideMapper;
import org.example.vroom.repositories.DriverRepository;
import org.example.vroom.repositories.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private DriverMapper driverMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DriverProfileMapper driverProfileMapper;
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private RideMapper rideMapper;

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

    public Collection<RideHistoryResponseDTO> getDriverRides(Long driverId,
                                                             LocalDateTime startDate,
                                                             LocalDateTime endDate,
                                                             Sort sort)
    {
        List<Ride> rides = rideRepository.findDriverRideHistory(driverId, startDate, endDate, sort);
        Collection<RideHistoryResponseDTO> rideHistoryResponseDTOs = new ArrayList<>();
        for (Ride ride : rides) {
            rideHistoryResponseDTOs.add(rideMapper.rideHistory(ride));
        }
        return rideHistoryResponseDTOs;
    }

}
