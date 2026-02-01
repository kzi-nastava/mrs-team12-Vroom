package org.example.vroom.controllers;

import org.example.vroom.entities.DriverLocation;
import org.example.vroom.services.DriverLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class DriverLocationController {

    @Autowired
    private DriverLocationService service;

    @GetMapping
    public List<DriverLocation> getAll() {
        return service.getAllLocations();
    }

    @PostMapping("/{driverId}")
    public void update(
            @PathVariable Long driverId,
            @RequestParam double lat,
            @RequestParam double lng
    ){
        service.updateLocation(driverId, lat, lng);
    }

    @GetMapping("/active")
    public List<DriverLocation> getActiveDrivers() {
        return service.getActiveDrivers();
    }

}
