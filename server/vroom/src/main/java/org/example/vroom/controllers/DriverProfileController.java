package org.example.vroom.controllers;

import org.example.vroom.DTOs.DriverDTO;
import org.example.vroom.DTOs.requests.auth.ChangePasswordRequestDTO;
import org.example.vroom.exceptions.auth.InvalidPasswordException;
import org.example.vroom.exceptions.user.DriverNotFoundException;
import org.example.vroom.services.DriverService;
import org.springframework.http.HttpStatus;
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
    //@PreAuthorize("hasAnyRole('DRIVER')")
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
    //@PreAuthorize("hasAnyRole('DRIVER')")
    public ResponseEntity<Void> requestUpdate(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody DriverDTO dto
    ) {
        driverService.requestProfileUpdate(userDetails.getUsername(), dto);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/change-password")
//@PreAuthorize("hasAnyRole('DRIVER')")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChangePasswordRequestDTO dto
    ) {
        try {
            driverService.changePassword(
                    userDetails.getUsername(),
                    dto.getOldPassword(),
                    dto.getNewPassword(),
                    dto.getConfirmNewPassword()
            );

            return ResponseEntity.ok("Password changed successfully");

        } catch (DriverNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (InvalidPasswordException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred");
        }
    }
}
