package org.example.vroom.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.SneakyThrows;
import org.example.vroom.DTOs.requests.driver.DriverUpdateRequestAdminDTO;
import org.example.vroom.DTOs.requests.driver.RejectRequestDTO;
import org.example.vroom.DTOs.responses.route.GetRouteResponseDTO;
import org.example.vroom.DTOs.responses.ride.RideHistoryResponseDTO;
import org.example.vroom.entities.DriverProfileUpdateRequest;
import org.example.vroom.enums.RequestStatus;
import org.example.vroom.enums.RideStatus;
import org.example.vroom.mappers.DriverMapper;
import org.example.vroom.repositories.DriverProfileUpdateRequestRepository;
import org.example.vroom.services.AdminService;
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
//@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private DriverProfileUpdateRequestRepository requestRepository;
    @Autowired
    private AdminService adminService;
    @Autowired
    private DriverMapper driverMapper;

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

    @PostMapping("/driver-update-requests/{id}/reject")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> reject(
            @PathVariable Long id,
            @RequestBody RejectRequestDTO dto
    ) {
        adminService.rejectRequest(id, dto.getComment());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/driver-update-requests")
    //@PreAuthorize("hasRole('ADMIN')")
    public List<DriverUpdateRequestAdminDTO> getPendingRequests()
            throws JsonProcessingException {
        return adminService.getPendingDriverRequests();
    }

    @PostMapping("/driver-update-requests/{id}/approve")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(adminService.approveRequest(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payload");
        }
    }

}
