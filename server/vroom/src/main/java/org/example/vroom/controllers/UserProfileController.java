package org.example.vroom.controllers;

import org.example.vroom.DTOs.RegisteredUserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile/user")
public class UserProfileController {

    @GetMapping
    public ResponseEntity<RegisteredUserDTO> getProfile() {
        RegisteredUserDTO dto = new RegisteredUserDTO();
        return ResponseEntity.ok(dto);
    }


    @PutMapping
    public ResponseEntity<Void> updateProfile(
            @RequestBody RegisteredUserDTO dto
    ) {
        return ResponseEntity.ok().build();
    }
}
