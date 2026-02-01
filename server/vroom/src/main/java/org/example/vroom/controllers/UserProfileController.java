package org.example.vroom.controllers;

import lombok.RequiredArgsConstructor;
import org.example.vroom.DTOs.RegisteredUserDTO;
import org.example.vroom.services.RegisteredUserService;
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
}
