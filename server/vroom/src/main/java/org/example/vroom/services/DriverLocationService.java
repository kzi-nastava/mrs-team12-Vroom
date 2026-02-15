package org.example.vroom.services;

import org.example.vroom.entities.Driver;
import org.example.vroom.entities.DriverLocation;
import org.example.vroom.exceptions.user.DriverNotFoundException;
import org.example.vroom.repositories.DriverLocationRepository;
import org.example.vroom.repositories.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DriverLocationService {

    @Autowired
    private DriverLocationRepository locationRepo;

    @Autowired
    private DriverRepository driverRepo;

    public void updateLocation(Long driverId, double lat, double lng) {

        Optional<Driver> driver = driverRepo.findById(driverId);
        if (driver.isEmpty()){
            return;
        }

        DriverLocation location = locationRepo
                .findByDriverId(driverId)
                .orElse(DriverLocation.builder()
                        .driver(driver.get())
                        .build());

        location.setLatitude(lat);
        location.setLongitude(lng);
        location.setLastUpdated(LocalDateTime.now());
        locationRepo.saveAndFlush(location);
    }

    public List<DriverLocation> getAllLocations() {
        return locationRepo.findAll();
    }

    public List<DriverLocation> getActiveDrivers() {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);

        return locationRepo.findAll().stream()
                .filter(loc -> loc.getLastUpdated().isAfter(fiveMinutesAgo))
                .toList();
    }

}

