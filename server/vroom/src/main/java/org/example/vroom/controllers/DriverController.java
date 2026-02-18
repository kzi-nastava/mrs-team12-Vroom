package org.example.vroom.controllers;

import jakarta.validation.Valid;
import org.example.vroom.DTOs.DriverDTO;
import org.example.vroom.DTOs.requests.driver.DriverChangeStatusRequestDTO;
import org.example.vroom.DTOs.requests.driver.DriverRegistrationRequestDTO;
import org.example.vroom.DTOs.requests.driver.SetPasswordRequestDTO;
import org.example.vroom.DTOs.responses.MessageResponseDTO;
import org.example.vroom.DTOs.responses.ride.GetRideResponseDTO;
import org.example.vroom.DTOs.responses.ride.RideHistoryMoreInfoResponseDTO;
import org.example.vroom.DTOs.responses.ride.RideHistoryResponseDTO;
import org.example.vroom.entities.Ride;
import org.example.vroom.entities.User;
import org.example.vroom.exceptions.ride.RideNotFoundException;
import org.example.vroom.exceptions.user.DriverAlreadyExistsException;
import org.example.vroom.exceptions.user.DriverStatusChangeNotAllowedException;
import org.example.vroom.exceptions.user.UserNotFoundException;
import org.example.vroom.mappers.RideMapper;
import org.example.vroom.services.DriverService;
import org.example.vroom.services.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api/drivers")
@Validated
public class DriverController {

    @Autowired
    private DriverService driverService;

    public DriverController() {}

    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN')")
    @GetMapping(path = "/rides", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<RideHistoryResponseDTO>> getRides(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String sort
    ) {
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Sort sortOrder = Sort.unsorted();
        if (sort != null && sort.contains(",")) {
            String[] split = sort.split(",");
            sortOrder = Sort.by(Sort.Direction.fromString(split[split.length - 1]), split[0]);
        }
        Collection<RideHistoryResponseDTO> rides = driverService.getDriverRides(user.getId(), startDate, endDate, sortOrder);

        if (rides == null || rides.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(rides, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'REGISTERED_USER', 'DRIVER')")
    @GetMapping(path="/more-info/{rideID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RideHistoryMoreInfoResponseDTO> getRideMoreInfo(
            @AuthenticationPrincipal User user,
            @PathVariable Long rideID
    ) {
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            RideHistoryMoreInfoResponseDTO rideInfo = driverService.getRideMoreInfo(rideID);
            return new ResponseEntity<>(rideInfo, HttpStatus.OK);
        }catch(RideNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register/driver")
    public ResponseEntity<?> registerDriver(@RequestBody DriverRegistrationRequestDTO request) {
        try {
            DriverDTO driver = driverService.registerDriver(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(driver);
        } catch (DriverAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/driver/set-password/{driverId}")
    public ResponseEntity<?> setDriverPassword(
            @PathVariable Long driverId,
            @RequestBody SetPasswordRequestDTO request) {

        try {
            driverService.setDriverPassword(driverId, request);
            return ResponseEntity.ok(Map.of("message", "Password set successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('DRIVER')")
    @PutMapping(path = "/status")
    public ResponseEntity<MessageResponseDTO> changeStatus(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody DriverChangeStatusRequestDTO data,
            BindingResult result
    ){
        if (result.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            driverService.changeStatus(user.getId(), data.getStatus());

            return new ResponseEntity<MessageResponseDTO>(
                    new MessageResponseDTO("Status changed"),
                    HttpStatus.OK
            );
        }catch(UserNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(DriverStatusChangeNotAllowedException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
