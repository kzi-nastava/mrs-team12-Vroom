package org.example.vroom.controllers;

import org.apache.coyote.Response;
import org.example.vroom.DTOs.DriverDTO;
import org.example.vroom.DTOs.requests.CancelRideDTO;
import org.example.vroom.DTOs.requests.StopRideDTO;
import org.example.vroom.DTOs.responses.*;
import org.example.vroom.enums.Gender;
import org.example.vroom.enums.RideStatus;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/rides")
public class RideController {

    @GetMapping(path="/{rideID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetRideDTO> getRide(@PathVariable Long rideID){
        DriverRideDTO driver = DriverRideDTO.builder()
                .firstName("Marko")
                .lastName("Markovic")
                .email("marko@example.com")
                .gender(Gender.MALE)
                .vehicle(null)
                .build();

        GetRouteDTO route = GetRouteDTO
                .builder()
                .startLocationLat(44.7866)
                .startLocationLng(20.4489)
                .endLocationLat(44.8125)
                .endLocationLng(20.4612)
                .stops(List.of())
                .build();

        GetRideDTO ride = GetRideDTO
                .builder()
                .route(route)
                .startTime(LocalDateTime.of(2025, 1, 10, 14, 32))
                .endTime(LocalDateTime.of(2025, 1, 10, 14, 55))
                .status(RideStatus.FINISHED)
                .price(980.50)
                .panicActivated(false)
                .driver(driver)
                .passengers(new ArrayList<>(List.of("Jovana Petrovic", "Nikola Ilic")))
                .complaints(new ArrayList<>(List.of()))
                .driverRating(5)
                .vehicleRating(4)
                .build();

        return new ResponseEntity<GetRideDTO>(ride, HttpStatus.OK);
    }


    @PutMapping(path = "/{rideID}/cancel", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponseDTO> cancelRide(
            @PathVariable Long rideID,
            @RequestBody CancelRideDTO data
    ){
        if(data==null) return new ResponseEntity<MessageResponseDTO>(
                new MessageResponseDTO("Data is missing"),
                HttpStatus.BAD_REQUEST
        );

        if(data.getType().equals("driver") && (data.getReason() == null || data.getReason().isEmpty())
        )
            return new ResponseEntity<MessageResponseDTO>(
                new MessageResponseDTO("Drivers must provide a reason for cancellation"),
                HttpStatus.BAD_REQUEST
            );

        return new ResponseEntity<MessageResponseDTO>(
                new MessageResponseDTO("Successfully cancelled ride"),
                HttpStatus.OK
        );
    }

    @PutMapping(path="/{rideID}/stop",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StoppedRideDTO> stopRide(
            @PathVariable Long rideID,
            @RequestBody StopRideDTO data
    ){
        if(data == null)
            return new ResponseEntity<StoppedRideDTO>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<StoppedRideDTO>(
                new StoppedRideDTO(),
                HttpStatus.OK
        );
    }

}
