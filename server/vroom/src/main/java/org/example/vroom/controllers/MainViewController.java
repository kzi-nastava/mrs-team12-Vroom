package org.example.vroom.controllers;

import org.example.vroom.DTOs.responses.DriverPositionDTO;
import org.example.vroom.DTOs.responses.PointResponseDTO;
import org.example.vroom.enums.DriverStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/api/main")
public class MainViewController {

    // 1. get all drivers positions
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<DriverPositionDTO>> getDriverPositions() {
        Collection<DriverPositionDTO> driverPositions = new ArrayList<>();
        DriverPositionDTO driverPositionDTO = new DriverPositionDTO();
        driverPositionDTO.setDriverId(1);
        driverPositionDTO.setStatus(DriverStatus.AVAILABLE);
        driverPositionDTO.setPoint(new PointResponseDTO(45.0,45.0));
        driverPositions.add(driverPositionDTO);

        DriverPositionDTO driverPositionDTO2 = new DriverPositionDTO();
        driverPositionDTO2.setDriverId(2);
        driverPositionDTO2.setStatus(DriverStatus.UNAVAILABLE);
        driverPositionDTO2.setPoint(new PointResponseDTO(49.76,41.32));
        driverPositions.add(driverPositionDTO2);

        return new ResponseEntity<>(driverPositions, HttpStatus.OK);
    }

    // 2. update all drivers positions
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<DriverPositionDTO>> updateDriverPositions(
            @RequestBody Collection<DriverPositionDTO> updates) {
        Collection<DriverPositionDTO> response = new ArrayList<>();

        for (DriverPositionDTO update : updates) {
            DriverPositionDTO driverPositionDTO = new DriverPositionDTO();
            driverPositionDTO.setDriverId(update.getDriverId());
            driverPositionDTO.setStatus(update.getStatus());
            driverPositionDTO.setPoint(update.getPoint());
            response.add(driverPositionDTO);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
