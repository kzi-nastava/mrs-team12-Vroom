package org.example.vroom.services;

import org.example.vroom.entities.Driver;
import org.example.vroom.entities.DriverLocation;
import org.example.vroom.repositories.DriverLocationRepository;
import org.example.vroom.repositories.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DriverLocationService {

    @Autowired
    private DriverLocationRepository locationRepo;

    @Autowired
    private DriverRepository driverRepo;

    public void updateLocation(Long driverId, double lat, double lng) {

        Driver driver = driverRepo.findById(driverId)
                .orElseThrow();

        DriverLocation location = locationRepo
                .findByDriverId(driverId)
                .orElse(DriverLocation.builder()
                        .driver(driver)
                        .build());

        location.setLatitude(lat);
        location.setLongitude(lng);
        location.setLastUpdated(LocalDateTime.now());

        locationRepo.save(location);
    }

    public List<DriverLocation> getAllLocations() {
        return locationRepo.findAll();
    }
}

