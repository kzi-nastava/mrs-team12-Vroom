package org.example.vroom.controllers;

import org.example.vroom.DTOs.responses.driver.DriverPositionDTO;
import org.example.vroom.DTOs.responses.ride.RideUpdateResponseDTO;
import org.example.vroom.DTOs.responses.route.PointResponseDTO;
import org.example.vroom.enums.DriverStatus;
import org.example.vroom.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class LocationSocketController {
    @Autowired
    DriverService driverService;

    @MessageMapping("update-location")
    @SendTo("/socket-publisher/location-updates")
    public DriverPositionDTO handleLocationUpdate(DriverPositionDTO location) {
        Optional<DriverStatus> statusOpt = this.driverService.getDriverStatus(location.getDriverId());
        if (statusOpt.isPresent()) {
            DriverStatus status = statusOpt.get();
            location.setStatus(status);
            if (status == DriverStatus.UNAVAILABLE || status == DriverStatus.AVAILABLE) {
                return location;
            }
        }
        return null;
    }
}