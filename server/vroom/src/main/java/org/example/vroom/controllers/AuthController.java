package org.example.vroom.controllers;

import org.example.vroom.DTOs.requests.*;
import org.example.vroom.DTOs.responses.*;
import org.example.vroom.entities.*;
import org.example.vroom.exceptions.AccountStatusException;
import org.example.vroom.exceptions.PasswordNotMatchException;
import org.example.vroom.exceptions.UserAlreadyExistsException;
import org.example.vroom.exceptions.UserDoesntExistException;
import org.example.vroom.mappers.*;
import org.example.vroom.services.RegisteredUserService;
import org.example.vroom.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private RegisteredUserService registeredUserService;
    @Autowired
    private DriverRegisterMapper driverRegisterMapper;

    @PostMapping(
            path="/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO data) {
        if(data==null)
            return new ResponseEntity<LoginResponseDTO>(HttpStatus.NO_CONTENT);

        try{
            LoginResponseDTO res = authService.login(data.getEmail(), data.getPassword());
            return new ResponseEntity<LoginResponseDTO>(res, HttpStatus.OK);

        }catch(UserDoesntExistException e){
            return new ResponseEntity<LoginResponseDTO>(HttpStatus.NOT_FOUND);
        }catch(PasswordNotMatchException e){
            return new ResponseEntity<LoginResponseDTO>(HttpStatus.UNAUTHORIZED);
        }catch(AccountStatusException e){
            return new ResponseEntity<LoginResponseDTO>(HttpStatus.FORBIDDEN);
        }
    }


    // init forget password process
    @PostMapping(
            path="/forgot-password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponseDTO> forgotPassword(@RequestBody ForgotPasswordRequestDTO data) {
        if(data==null)
            return new ResponseEntity<MessageResponseDTO>(HttpStatus.NO_CONTENT);

        Token token = Token.builder()
                .code("test123")
                .user(new RegisteredUser())
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plus(15, ChronoUnit.MINUTES))
                .build();
        // save token and send code to email
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
            return new ResponseEntity<MessageResponseDTO>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<MessageResponseDTO>(
                new MessageResponseDTO("Successfully reset password"),
                HttpStatus.OK
        );
    }

    @PostMapping(
            path="/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponseDTO> register(@RequestBody RegisterRequestDTO data){
        if(data == null)
            return new ResponseEntity<MessageResponseDTO>(HttpStatus.NO_CONTENT);

        try{
            registeredUserService.createUser(data);
            return new ResponseEntity<MessageResponseDTO>(
                    new MessageResponseDTO("Successfully create user, activation link is sent to email"),
                    HttpStatus.CREATED
            );
        }catch(UserAlreadyExistsException e) {
            return new ResponseEntity<MessageResponseDTO>(
                    new MessageResponseDTO(e.getMessage()),
                    HttpStatus.CONFLICT
            );
        } catch (RuntimeException e){
            return new ResponseEntity<MessageResponseDTO>(
                    new MessageResponseDTO(e.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }

    }

    @GetMapping(path = "/activate-account/{userID}")
    public ResponseEntity<Void> activateAccount(@PathVariable Long userID){
        boolean isActivated = registeredUserService.activateUser(userID);
        String targetUrl="";

        if(isActivated)
            targetUrl = "http://localhost:4200/login?status=activated";
        else
            targetUrl = "http://localhost:4200/login?status=failed";

        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .location(URI.create(targetUrl))
                .build();
    }
    
    @PostMapping(
            path = "/register/driver",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RegisterResponseDTO> registerDriver(
            @RequestBody DriverRegisterRequestDTO data
    ) {
        if (data == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        Driver driver = driverRegisterMapper.toEntity(data);
        // driverRepository.save(driver);

        return new ResponseEntity<RegisterResponseDTO>(
                new RegisterResponseDTO(
                        1L,
                        "Successfully created driver account, before login activate account"
                ),
                HttpStatus.CREATED
        );
    }


    @PostMapping(
            path="/logout",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MessageResponseDTO> logout(@RequestBody LogoutRequestDTO req) {
        try{
            authService.logout(Long.valueOf(req.getId()), req.getType());
            return new ResponseEntity<MessageResponseDTO>(
                    new MessageResponseDTO("Logout successful"),
                    HttpStatus.OK
            );
        }catch(RuntimeException e){
            return new ResponseEntity<MessageResponseDTO>(
                new MessageResponseDTO(e.getMessage()),
                    HttpStatus.NOT_FOUND
            );
        }
    }
}
