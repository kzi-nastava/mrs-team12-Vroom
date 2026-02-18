package org.example.vroom.controllers;

import org.example.vroom.DTOs.responses.driver.DriverPositionDTO;
import org.example.vroom.DTOs.responses.ride.RideUpdateResponseDTO;
import org.example.vroom.DTOs.responses.route.PointResponseDTO;
import org.example.vroom.entities.User;
import org.example.vroom.enums.DriverStatus;
import org.example.vroom.services.DriverLocationService;
import org.example.vroom.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.Optional;

import static org.springframework.security.authorization.AuthorityReactiveAuthorizationManager.hasRole;

@Controller
public class LocationSocketController {
    @Autowired
    DriverService driverService;

    @Autowired
    DriverLocationService driverLocationService;

    @MessageMapping("update-location")
    @SendTo("/socket-publisher/location-updates")
    public DriverPositionDTO handleLocationUpdate(
            SimpMessageHeaderAccessor headerAccessor,
            @Payload PointResponseDTO location
    ) {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) headerAccessor.getUser();
        if(auth == null || auth.getPrincipal() == null ){
            return null;
        }
        User user = (User) auth.getPrincipal();
        if (!"DRIVER".equals(user.getRoleName())){
            return null;
        }
        driverLocationService.updateLocation(user.getId(), location.getLat(), location.getLng());

        Optional<DriverStatus> statusOpt = this.driverService.getDriverStatus(user.getId());
        DriverPositionDTO driverPositionDTO = new DriverPositionDTO();
        driverPositionDTO.setDriverId(user.getId());
        driverPositionDTO.setPoint(location);
        if (statusOpt.isPresent()) {
            DriverStatus status = statusOpt.get();
            driverPositionDTO.setStatus(status);
            if (status == DriverStatus.UNAVAILABLE || status == DriverStatus.AVAILABLE) {
                return driverPositionDTO;
            }
        }
        return null;
    }
}