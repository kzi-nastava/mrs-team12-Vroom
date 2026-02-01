package org.example.vroom.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.example.vroom.DTOs.requests.auth.*;
import org.example.vroom.DTOs.requests.driver.DriverRegisterRequestDTO;
import org.example.vroom.DTOs.responses.auth.LoginResponseDTO;
import org.example.vroom.DTOs.responses.auth.RegisterResponseDTO;
import org.example.vroom.enums.DriverStatus;
import org.example.vroom.exceptions.auth.InvalidPasswordException;
import org.example.vroom.exceptions.registered_user.ActivationExpiredException;
import org.example.vroom.repositories.DriverRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.example.vroom.DTOs.responses.*;
import org.example.vroom.entities.*;
import org.example.vroom.exceptions.auth.InvalidTokenException;
import org.example.vroom.exceptions.auth.TokenPresentException;
import org.example.vroom.exceptions.user.AccountStatusException;
import org.example.vroom.exceptions.user.UserAlreadyExistsException;
import org.example.vroom.exceptions.user.UserNotFoundException;
import org.example.vroom.mappers.*;
import org.example.vroom.services.RegisteredUserService;
import org.example.vroom.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private RegisteredUserService registeredUserService;
    @Autowired
    private DriverRegisterMapper driverRegisterMapper;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping(
            path="/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO data, HttpServletResponse response) {
        if(data==null)
            return new ResponseEntity<LoginResponseDTO>(HttpStatus.NO_CONTENT);

        try{
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(data.getEmail(), data.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            LoginResponseDTO res = authService.login((User) authentication.getPrincipal(), response);

            return new ResponseEntity<LoginResponseDTO>(res, HttpStatus.OK);

        }catch(BadCredentialsException | UserNotFoundException | InternalAuthenticationServiceException e){
            return new ResponseEntity<LoginResponseDTO>(HttpStatus.UNAUTHORIZED);
        }catch(AccountStatusException | DisabledException | LockedException e){
            return new ResponseEntity<LoginResponseDTO>(HttpStatus.FORBIDDEN);
        } catch(Exception e){
            return new ResponseEntity<LoginResponseDTO>(HttpStatus.INTERNAL_SERVER_ERROR);
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

        try{
            authService.forgotPassword(data.getEmail());
            return new ResponseEntity<MessageResponseDTO>(
                    new MessageResponseDTO("Check email for the code. Redirecting...."),
                    HttpStatus.CREATED
            );
        }catch(UserNotFoundException e){
            return new ResponseEntity<MessageResponseDTO>(new MessageResponseDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        }catch(TokenPresentException e){
            return new ResponseEntity<MessageResponseDTO>(new MessageResponseDTO(e.getMessage()), HttpStatus.CONFLICT);
        } catch(RuntimeException e){
            return new ResponseEntity<MessageResponseDTO>(new MessageResponseDTO(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }catch(Exception e){
            return new ResponseEntity<MessageResponseDTO>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // finish forget password process
    @PutMapping(
            path = "/reset-password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponseDTO> forgotPassword(@RequestBody ResetPasswordRequestDTO data){
        if(data==null)
            return new ResponseEntity<MessageResponseDTO>(HttpStatus.NO_CONTENT);

        try{
            authService.resetPassword(data.getEmail(), data.getCode(), data.getPassword(), data.getConfirmPassword());
            return new ResponseEntity<MessageResponseDTO>(
                    new MessageResponseDTO("Successfully reseted password"),
                    HttpStatus.OK
            );
        }catch(InvalidTokenException | InvalidPasswordException e){
            return new ResponseEntity<MessageResponseDTO>(new MessageResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            return new ResponseEntity<MessageResponseDTO>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping(
            path="/register",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponseDTO> register(
            @ModelAttribute RegisterRequestDTO data,
            @RequestPart(value = "profilePhoto", required = false) MultipartFile profilePhoto
    ){
        if(data == null)
            return new ResponseEntity<MessageResponseDTO>(HttpStatus.NO_CONTENT);

        try{
            registeredUserService.createUser(data, profilePhoto);
            return new ResponseEntity<MessageResponseDTO>(
                    new MessageResponseDTO("Successfully created user, activation link is sent to email"),
                    HttpStatus.CREATED
            );
        } catch(UserAlreadyExistsException e) {
            return new ResponseEntity<MessageResponseDTO>(new MessageResponseDTO(e.getMessage()), HttpStatus.CONFLICT);
        } catch(InvalidPasswordException e){
            return new ResponseEntity<>(new MessageResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e){
            return new ResponseEntity<MessageResponseDTO>(new MessageResponseDTO(e.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
        } catch(Exception e){
            return new ResponseEntity<MessageResponseDTO>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/activate-account/{userID}")
    public ResponseEntity<Void> activateAccount(@PathVariable Long userID){
        String targetUrl;

        try {
            registeredUserService.activateUser(userID);

            targetUrl = "http://localhost:4200/login?status=activated";
        } catch (ActivationExpiredException e) {
            registeredUserService.deleteAccount(userID);
            targetUrl = "http://localhost:4200/login?status=expired";
        } catch (UserNotFoundException e) {
            targetUrl = "http://localhost:4200/login?status=failed";
        }

        return ResponseEntity
                .status(HttpStatus.SEE_OTHER)
                .location(URI.create(targetUrl))
                .build();
    }

    @PostMapping(
            path="/logout",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MessageResponseDTO> logout(@AuthenticationPrincipal User user) {
        try{
            authService.logout(user);
            return new ResponseEntity<MessageResponseDTO>(
                    new MessageResponseDTO("Logout successful"),
                    HttpStatus.OK
            );
        }catch(UserNotFoundException e){
            return new ResponseEntity<MessageResponseDTO>(new MessageResponseDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<MessageResponseDTO>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
