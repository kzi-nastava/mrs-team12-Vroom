package org.example.vroom.controllers;

import org.example.vroom.DTOs.OrderFromFavoriteRequestDTO;
import org.example.vroom.DTOs.RideDTO;
import org.example.vroom.DTOs.requests.*;
import org.example.vroom.DTOs.responses.MessageResponseDTO;
import org.example.vroom.DTOs.responses.StoppedRideResponseDTO;
import org.example.vroom.entities.Driver;
import org.example.vroom.entities.Ride;
import org.example.vroom.entities.Route;
import org.example.vroom.DTOs.requests.CancelRideRequestDTO;
import org.example.vroom.DTOs.requests.StopRideRequestDTO;
import org.example.vroom.DTOs.responses.*;
import org.example.vroom.enums.DriverStatus;
import org.example.vroom.enums.Gender;
import org.example.vroom.enums.RideStatus;
import org.example.vroom.exceptions.user.NoAvailableDriverException;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.example.vroom.services.RideService;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/rides")
public class RideController {
    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @GetMapping(path="/{rideID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetRideResponseDTO> getRide(@PathVariable Long rideID){
        DriverRideResponseDTO driver = DriverRideResponseDTO.builder()
                .firstName("Marko")
                .lastName("Markovic")
                .email("marko@example.com")
                .gender(Gender.MALE)
                .vehicle(null)
                .build();

        GetRouteResponseDTO route = GetRouteResponseDTO
                .builder()
                .startLocationLat(44.7866)
                .startLocationLng(20.4489)
                .endLocationLat(44.8125)
                .endLocationLng(20.4612)
                .stops(List.of())
                .build();

        GetRideResponseDTO ride = GetRideResponseDTO
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

        return new ResponseEntity<GetRideResponseDTO>(ride, HttpStatus.OK);
    }

    @GetMapping(path = "/{rideID}/duration", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetRideUpdatesResponseDTO> getRideUpdate(@PathVariable Long rideID){
        GetRideUpdatesResponseDTO dto = new GetRideUpdatesResponseDTO();
        dto.setPoint(new PointResponseDTO(48.45, 45.32));
        dto.setTime(14.0);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PutMapping(path = "/{rideID}/duration", consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponseDTO> updateRide(
            @PathVariable Long rideID,
            @RequestBody RideUpdateRequestDTO updatedData
    ){
        double time = updatedData.getTime();
        PointResponseDTO point = updatedData.getPoint();
        return new ResponseEntity<>(new MessageResponseDTO("Success"), HttpStatus.OK);
    }

    @PostMapping(path = "/{rideID}/complaint", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponseDTO> sendComplaint(
            @PathVariable Long rideID,
            @RequestBody ComplaintRequestDTO complaint
    ){
        Ride ride = new Ride();
        Driver driver = new Driver();
        ride.setId(rideID);
        ride.setStatus(RideStatus.FINISHED);
        ride.setDriver(driver);
        ride.getDriver().setStatus(DriverStatus.AVAILABLE);
        return new ResponseEntity<>(new MessageResponseDTO("Success"), HttpStatus.OK);
    }

    @PostMapping(path = "/{rideID}/review", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponseDTO> leaveReview(
            @PathVariable Long rideID,
            @RequestBody LeaveReviewRequestDTO review
    ){
        rideService.leaveReview(rideID, review);
        return new ResponseEntity<>(new MessageResponseDTO("Success"), HttpStatus.OK);
    }

    @PostMapping(path = "/{rideID}/finish")
    public ResponseEntity<MessageResponseDTO> finishRide(
            @PathVariable Long rideID
    ){
        // ridestatus = finished
        // driverstatus = available
        return new ResponseEntity<>(new MessageResponseDTO("Success"), HttpStatus.OK);
    }


    @PutMapping(path = "/{rideID}/cancel", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponseDTO> cancelRide(
            @PathVariable Long rideID,
            @RequestBody CancelRideRequestDTO data
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
    public ResponseEntity<StoppedRideResponseDTO> stopRide(
            @PathVariable Long rideID,
            @RequestBody StopRideRequestDTO data
    ){
        if(data == null)
            return new ResponseEntity<StoppedRideResponseDTO>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<StoppedRideResponseDTO>(
                new StoppedRideResponseDTO(),
                HttpStatus.OK
        );
    }


   // @PreAuthorize("hasRole('USER')")
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GetRideResponseDTO> orderRide(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody RideRequestDTO request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        rideService.orderRide(
                                userDetails.getUsername(),
                                request
                        )
                );
    }

    @ExceptionHandler(NoAvailableDriverException.class)
    public ResponseEntity<MessageResponseDTO> handleNoDriver(NoAvailableDriverException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponseDTO("Ride order declined: " + ex.getMessage()));
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
