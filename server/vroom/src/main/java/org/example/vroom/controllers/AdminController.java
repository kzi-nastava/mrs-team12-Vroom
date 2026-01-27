package org.example.vroom.controllers;

import org.example.vroom.DTOs.responses.route.GetRouteResponseDTO;
import org.example.vroom.DTOs.responses.ride.RideHistoryResponseDTO;
import org.example.vroom.entities.DriverProfileUpdateRequest;
import org.example.vroom.enums.RequestStatus;
import org.example.vroom.enums.RideStatus;
import org.example.vroom.repositories.DriverProfileUpdateRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@RestController
@RequestMapping("/api/admins")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private DriverProfileUpdateRequestRepository requestRepository;

    @GetMapping(path = "/users/{userID}/rides", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<RideHistoryResponseDTO>> getRides(
            @PathVariable Long userID,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) String sort
    ) {
        Collection<RideHistoryResponseDTO> rides = new ArrayList<RideHistoryResponseDTO>();
        GetRouteResponseDTO route1 = GetRouteResponseDTO
                .builder()
                .startLocationLat(44.7866)
                .startLocationLng(20.4489)
                .endLocationLat(44.8125)
                .endLocationLng(20.4612)
                .stops(List.of())
                .build();

        RideHistoryResponseDTO ride1 = RideHistoryResponseDTO
                .builder()
                .startTime(LocalDateTime.of(2025, 1, 10, 14, 32))
                .status(RideStatus.FINISHED)
                .price(980.50)
                .panicActivated(false)
                .build();

        rides.add(ride1);

        return new ResponseEntity<Collection<RideHistoryResponseDTO>>(rides, HttpStatus.OK);
    }

    @GetMapping("/admin/driver-update-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DriverProfileUpdateRequest> getPendingRequests() {
        return requestRepository.findAll()
                .stream()
                .filter(r -> r.getStatus() == RequestStatus.PENDING)
                .toList();
    }
}
