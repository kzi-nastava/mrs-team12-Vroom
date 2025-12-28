package org.example.vroom.controllers;

import org.apache.coyote.Response;
import org.example.vroom.DTOs.OrderFromFavoriteRequestDTO;
import org.example.vroom.DTOs.RideDTO;
import org.example.vroom.DTOs.requests.CancelRideDTO;
import org.example.vroom.DTOs.requests.RideRequestDTO;
import org.example.vroom.DTOs.requests.StartRideRequestDTO;
import org.example.vroom.DTOs.requests.StopRideDTO;
import org.example.vroom.DTOs.responses.MessageResponseDTO;
import org.example.vroom.DTOs.responses.StoppedRideDTO;
import org.example.vroom.entities.Ride;
import org.example.vroom.entities.Route;
import org.example.vroom.enums.RideStatus;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import javax.swing.text.html.parser.Entity;


@RestController
@RequestMapping("/api/rides")
public class RideController {

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
    
    
    @PostMapping(
            path = "/order",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RideDTO> orderRide(
            @RequestBody RideRequestDTO request
    ) {
        if (request == null || request.getRoute() == null) {
            return ResponseEntity.badRequest().build();
        }


        RideDTO response = RideDTO.builder()
                .id(1L)
                .status(RideStatus.PENDING)
                .price(1500.0)
                .build();

        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{rideId}/start")
    public ResponseEntity<RideDTO> startRide(
            @PathVariable Long rideId,
            @RequestBody(required = false) StartRideRequestDTO dto
    ) {

        Ride ride = new Ride();
        ride.setId(rideId);
        ride.setStatus(RideStatus.ACCEPTED);


        if (ride.getStatus() != RideStatus.ACCEPTED) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        ride.setStartTime(
                dto != null && dto.getStartTime() != null
                        ? dto.getStartTime()
                        : LocalDateTime.now()
        );

        ride.setStatus(RideStatus.ONGOING);

        RideDTO response = RideDTO.builder()
                .id(ride.getId())
                .status(ride.getStatus())
                .startTime(ride.getStartTime())
                .build();

        return ResponseEntity.ok(response);
    }
    
    @PostMapping(
            path = "/order/favorite",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RideDTO> orderFromFavorite(
            @RequestBody OrderFromFavoriteRequestDTO dto
    ) {

        if (dto == null || dto.getFavoriteRouteId() == null) {
            return ResponseEntity.badRequest().build();
        }

        Route route = new Route();

        Ride ride = Ride.builder()
                .route(route)
                .status(RideStatus.PENDING)
                .isScheduled(dto.getScheduledTime() != null)
                .build();

        RideDTO response = RideDTO.builder()
                .id(1L)
                .status(ride.getStatus())
                .build();

        return ResponseEntity.ok(response);
    }

}
