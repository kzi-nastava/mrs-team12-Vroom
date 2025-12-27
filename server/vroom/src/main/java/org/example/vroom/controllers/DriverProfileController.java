package org.example.vroom.controllers;

import org.example.vroom.DTOs.DriverDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile/driver")
public class DriverProfileController {

	@GetMapping
	public ResponseEntity<DriverDTO> getProfile() {
	    DriverDTO dto = new DriverDTO(); 
	    return ResponseEntity.ok(dto);
	}

    @PutMapping
    public ResponseEntity<Void> updateProfile(
            @RequestBody DriverDTO dto
    ) {
        return ResponseEntity.ok().build();
    }
}
