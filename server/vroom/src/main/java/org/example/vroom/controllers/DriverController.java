package org.example.vroom.controllers;

import org.example.vroom.DTOs.DriverDTO;
import org.example.vroom.DTOs.requests.driver.DriverChangeStatusRequestDTO;
import org.example.vroom.DTOs.requests.driver.DriverRegistrationRequestDTO;
import org.example.vroom.DTOs.responses.MessageResponseDTO;
import org.example.vroom.DTOs.responses.ride.GetRideResponseDTO;
import org.example.vroom.DTOs.responses.ride.RideHistoryResponseDTO;
import org.example.vroom.entities.Ride;
import org.example.vroom.entities.User;
import org.example.vroom.exceptions.user.DriverAlreadyExistsException;
import org.example.vroom.exceptions.user.DriverStatusChangeNotAllowedException;
import org.example.vroom.exceptions.user.UserNotFoundException;
import org.example.vroom.mappers.RideMapper;
import org.example.vroom.services.DriverService;
import org.example.vroom.services.RideService;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;


    public DriverController(DriverService driverService, RideService rideService, RideMapper rideMapper) {
        this.driverService = driverService;
    }

    @GetMapping(path = "/rides", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<RideHistoryResponseDTO>> getRides(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String sort
    ) {
        System.out.println("getRides AAAAAAAAAAAAAAAAAAA");
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

    //@PreAuthorize("hasRole('ADMIN')")
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

    @PutMapping(path = "/status")
    public ResponseEntity<MessageResponseDTO> changeStatus(
            @AuthenticationPrincipal User user,
            @RequestBody DriverChangeStatusRequestDTO data
    ){
        if(data == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
