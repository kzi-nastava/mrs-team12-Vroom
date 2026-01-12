package org.example.vroom.controllers;

import org.example.vroom.DTOs.RegisteredUserDTO;
import org.example.vroom.services.RegisteredUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile/user")
public class UserProfileController {

    RegisteredUserService registeredUserService;

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
