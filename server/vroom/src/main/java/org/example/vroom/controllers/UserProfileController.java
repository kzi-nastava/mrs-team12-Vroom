package org.example.vroom.controllers;

import org.example.vroom.DTOs.RegisteredUserDTO;
import org.example.vroom.DTOs.requests.auth.ChangePasswordRequestDTO;
import org.example.vroom.exceptions.auth.InvalidPasswordException;
import org.example.vroom.exceptions.user.DriverNotFoundException;
import org.example.vroom.services.RegisteredUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(
        origins = "http://localhost:4200",
        allowCredentials = "true"
)
@RestController
@RequestMapping("/api/profile/user")
@PreAuthorize("hasRole('REGISTERED_USER')")
public class UserProfileController {

    //RegisteredUserService registeredUserService;
    private final RegisteredUserService registeredUserService;

    public UserProfileController(RegisteredUserService registeredUserService) {
        this.registeredUserService = registeredUserService;
    }
    @GetMapping("/me")
    public ResponseEntity<RegisteredUserDTO> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(
                registeredUserService.getMyProfile(
                        userDetails.getUsername()
                )
        );
    }


    @PutMapping("/me")
    public ResponseEntity<RegisteredUserDTO> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody RegisteredUserDTO dto
    ) {
        return ResponseEntity.ok(
                registeredUserService.updateMyProfile(
                        userDetails.getUsername(),
                        dto
                )
        );
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChangePasswordRequestDTO dto
    ) {
        try {
            registeredUserService.changePassword(
                    userDetails.getUsername(),
                    dto.getOldPassword(),
                    dto.getNewPassword(),
                    dto.getConfirmNewPassword()
            );
            return ResponseEntity.ok("Password changed successfully");
        } catch (
                DriverNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        } catch (
                InvalidPasswordException e) {
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
