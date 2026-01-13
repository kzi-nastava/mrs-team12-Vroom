package org.example.vroom.controllers;

import org.example.vroom.DTOs.DriverDTO;
import org.example.vroom.DTOs.requests.DriverRegistrationRequestDTO;
import org.example.vroom.DTOs.responses.GetRouteResponseDTO;
import org.example.vroom.DTOs.responses.RideHistoryResponseDTO;
import org.example.vroom.enums.RideStatus;
import org.example.vroom.services.DriverService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping(path = "/{driverID}/rides", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<RideHistoryResponseDTO>> getRides(
            @PathVariable Long driverID,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) Sort sort
    ) {
        Collection<RideHistoryResponseDTO> rides = driverService.getDriverRides(driverID, startDate, endDate, sort);
        return new ResponseEntity<>(rides, HttpStatus.OK);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<DriverDTO> registerDriver(
            @RequestBody DriverRegistrationRequestDTO request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(driverService.registerDriver(request));
    }

}
