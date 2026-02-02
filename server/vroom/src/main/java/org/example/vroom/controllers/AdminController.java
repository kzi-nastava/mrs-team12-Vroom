package org.example.vroom.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.SneakyThrows;
import org.example.vroom.DTOs.BlockUserRequestDTO;
import org.example.vroom.DTOs.requests.driver.DriverUpdateRequestAdminDTO;
import org.example.vroom.DTOs.requests.driver.RejectRequestDTO;
import org.example.vroom.DTOs.responses.AdminUserDTO;
import org.example.vroom.DTOs.responses.route.GetRouteResponseDTO;
import org.example.vroom.DTOs.responses.ride.RideHistoryResponseDTO;
import org.example.vroom.DTOs.responses.user.UserRideHistoryResponseDTO;
import org.example.vroom.entities.DriverProfileUpdateRequest;
import org.example.vroom.enums.RequestStatus;
import org.example.vroom.enums.RideStatus;
import org.example.vroom.exceptions.user.UserNotFoundException;
import org.example.vroom.mappers.DriverMapper;
import org.example.vroom.repositories.DriverProfileUpdateRequestRepository;
import org.example.vroom.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

    @GetMapping(path = "/users/rides", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserRideHistoryResponseDTO>> getRides(
            @RequestParam(required = false) String userEmail,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {
        try{
            List<UserRideHistoryResponseDTO> rides;

            if(userEmail != null && !userEmail.isEmpty())
                rides = adminService.getUserRideHistory(userEmail, sort, startDate, endDate, pageNumber, pageSize);
            else
                rides = adminService.getUserRideHistory(sort, startDate, endDate, pageNumber, pageSize);

            return new ResponseEntity<List<UserRideHistoryResponseDTO>>(rides, HttpStatus.OK);
        }catch(UserNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

    @GetMapping("/users")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminUserDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }


    @PutMapping("/users/{id}/block")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> blockUser(
            @PathVariable Long id,
            @RequestBody BlockUserRequestDTO dto
    ) {
        adminService.blockUser(id, dto.getReason());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}/unblock")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unblockUser(@PathVariable Long id) {
        adminService.unblockUser(id);
        return ResponseEntity.noContent().build();
    }

}
