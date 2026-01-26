package org.example.vroom.controllers;

import org.example.vroom.DTOs.DriverDTO;
import org.example.vroom.services.DriverService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile/driver")
public class DriverProfileController {
    private final DriverService driverService;

    public DriverProfileController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DriverDTO> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(
                driverService.getMyProfile(
                        userDetails.getUsername()
                )
        );
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Void> requestUpdate(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody DriverDTO dto
    ) {
        driverService.requestProfileUpdate(userDetails.getUsername(), dto);
        return ResponseEntity.accepted().build();
    }
}
