package org.example.vroom.controllers;

import org.example.vroom.DTOs.responses.ride.RideResponseDTO;
import org.example.vroom.entities.User;
import org.example.vroom.services.RegisteredUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/api/registered-user")
@PreAuthorize("hasRole('REGISTERED_USER')")
public class RegisteredUserController {
    @Autowired
    private RegisteredUserService registeredUserService;

    @GetMapping("/rides")
    public ResponseEntity<List<RideResponseDTO>> getRideHistory(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(required = false, defaultValue = "10") int pageSize
    ){
        List<RideResponseDTO> rides = registeredUserService.getUserRideHistory(
                user, sort, startDate, endDate, pageNumber, pageSize
        );

        return new ResponseEntity<List<RideResponseDTO>>(rides, HttpStatus.OK);
    }

}
