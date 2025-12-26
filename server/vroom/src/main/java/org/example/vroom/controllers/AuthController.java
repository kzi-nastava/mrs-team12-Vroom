package org.example.vroom.controllers;

import org.example.vroom.DTOs.requests.*;
import org.example.vroom.DTOs.responses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping(
            path="/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginData) {
        LoginResponseDTO res = LoginResponseDTO.builder()
                                            .userID(1L)
                                            .token("token_test")
                                            .type("user")
                                            .expiresIn(1000L)
                                            .build();

        return ResponseEntity.ok(res);
    }

}
