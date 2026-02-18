package org.example.vroom.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import javassist.NotFoundException;
import org.example.vroom.DTOs.requests.ride.CreateFavoriteRouteRequestDTO;
import org.example.vroom.DTOs.requests.ride.FavoriteRouteResponseDTO;
import org.example.vroom.DTOs.responses.ride.RideResponseDTO;
import org.example.vroom.entities.RegisteredUser;
import org.example.vroom.entities.User;
import org.example.vroom.services.FavoriteRouteService;
import org.example.vroom.services.RegisteredUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registered-user")
@PreAuthorize("hasRole('REGISTERED_USER')")
@Validated
public class RegisteredUserController {

    @Autowired
    private RegisteredUserService registeredUserService;

    @Autowired
    private FavoriteRouteService favoriteRouteService;

    @GetMapping("/rides")
    public ResponseEntity<List<RideResponseDTO>> getRideHistory(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false, defaultValue = "0") @Min(value = 0) int pageNumber,
            @RequestParam(required = false, defaultValue = "10") @Min(value = 1) int pageSize
    ){
        List<RideResponseDTO> rides = registeredUserService.getUserRideHistory(
                user, sort, startDate, endDate, pageNumber, pageSize
        );

        return new ResponseEntity<>(rides, HttpStatus.OK);
    }



    @PostMapping("/favorite-routes")
    public ResponseEntity<?> addFavoriteRoute(
            @AuthenticationPrincipal User user,
            @RequestBody CreateFavoriteRouteRequestDTO request
    ) {
        try {
            RegisteredUser registeredUser = (RegisteredUser) user;
            FavoriteRouteResponseDTO favorite = favoriteRouteService.createFavoriteFromRide(request, registeredUser);
            return new ResponseEntity<>(favorite, HttpStatus.CREATED);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(
                    Map.of("message", "Ride not found"),
                    HttpStatus.NOT_FOUND
            );
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                    Map.of("message", e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    Map.of("message", "Failed to add route to favorites"),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

}
