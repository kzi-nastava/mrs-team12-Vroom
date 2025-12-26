package org.example.vroom.controllers;

import org.aspectj.bridge.Message;
import org.example.vroom.DTOs.requests.*;
import org.example.vroom.DTOs.responses.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping(
            path="/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO data) {
        LoginResponseDTO res = LoginResponseDTO.builder()
                                            .userID(1L)
                                            .token("token_test")
                                            .type("user")
                                            .expiresIn(1000L)
                                            .build();
        if(data==null)
            return new  ResponseEntity<LoginResponseDTO>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<LoginResponseDTO>(res, HttpStatus.OK);
    }


    // init forget password process
    @PostMapping(
            path="/forgot-password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponseDTO> forgotPassword(@RequestBody ForgotPasswordRequestDTO data) {
        if(data==null)
            return new  ResponseEntity<MessageResponseDTO>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<MessageResponseDTO>(
                new MessageResponseDTO("Check email for the code"),
                HttpStatus.OK
        );
    }

    // finish forget password process
    @PutMapping(
            path = "/reset-password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponseDTO> forgotPassword(@RequestBody ResetPasswordRequestDTO data){
        if(data==null)
            return new  ResponseEntity<MessageResponseDTO>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<MessageResponseDTO>(
                new MessageResponseDTO("Successfully reset password"),
                HttpStatus.OK
        );
    }
}
