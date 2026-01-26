package org.example.vroom.controllers;

import org.example.vroom.DTOs.FavoriteRouteDTO;
import org.example.vroom.DTOs.OrderFromFavoriteRequestDTO;
import org.example.vroom.DTOs.RideDTO;
import org.example.vroom.DTOs.requests.ride.*;
import org.example.vroom.DTOs.responses.MessageResponseDTO;
import org.example.vroom.DTOs.responses.ride.GetRideUpdatesResponseDTO;
import org.example.vroom.DTOs.responses.ride.StoppedRideResponseDTO;
import org.example.vroom.DTOs.responses.driver.DriverRideResponseDTO;
import org.example.vroom.DTOs.responses.ride.GetRideResponseDTO;
import org.example.vroom.DTOs.responses.route.GetRouteResponseDTO;
import org.example.vroom.DTOs.responses.route.PointResponseDTO;
import org.example.vroom.entities.FavoriteRoute;
import org.example.vroom.entities.Ride;
import org.example.vroom.entities.Route;
import org.example.vroom.enums.Gender;
import org.example.vroom.enums.RideStatus;
import org.example.vroom.exceptions.ride.*;
import org.example.vroom.exceptions.user.NoAvailableDriverException;
import org.example.vroom.mappers.RouteMapper;
import org.example.vroom.repositories.RideRepository;
import org.example.vroom.services.FavoriteRouteService;
import org.example.vroom.services.RouteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final RideRepository rideRepository;
    private final RouteMapper routeMapper;
    private final FavoriteRouteService favoriteRouteService;
    private static final Logger log = LoggerFactory.getLogger(RideService.class);


    public RideController(RideService rideService, RideRepository rideRepository, RouteMapper routeMapper, FavoriteRouteService favoriteRouteService) {
        this.rideService = rideService;
        this.rideRepository = rideRepository;
        this.routeMapper = routeMapper;
        this.favoriteRouteService = favoriteRouteService;
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
        try {
            this.rideService.sendComplaint(rideID, complaint);
        } catch (EmptyBodyException e) {
            return new ResponseEntity<>(new MessageResponseDTO("Complaint Body is empty"), HttpStatus.BAD_REQUEST);
        } catch (RideNotFoundException e) {
            return new ResponseEntity<>(new MessageResponseDTO("Ride Not Found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new MessageResponseDTO("Success"), HttpStatus.OK);
    }

    @PostMapping(path = "/{rideID}/review", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponseDTO> leaveReview(
            @PathVariable Long rideID,
            @RequestBody LeaveReviewRequestDTO review
    ){
        System.out.println(rideID);
        if (review == null){
            return new ResponseEntity<>(new MessageResponseDTO("No content sent"), HttpStatus.NO_CONTENT);
        }
        try {
            rideService.leaveReview(rideID, review);
        }catch (RideNotFoundException e){
            return new ResponseEntity<>(new MessageResponseDTO("Ride not found"), HttpStatus.NOT_FOUND);
        }catch (CantReviewRideException e){
            return new ResponseEntity<>(new MessageResponseDTO("Cant review ride"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new MessageResponseDTO("Success"), HttpStatus.OK);
    }

    @PostMapping(path = "/{rideID}/finish")
    public ResponseEntity<MessageResponseDTO> finishRide(
            @PathVariable Long rideID
    ){
        try {
            rideService.finishRide(rideID);
        }catch (RideNotFoundException e){
            return new ResponseEntity<>(new MessageResponseDTO("Ride not found"), HttpStatus.NOT_FOUND);
        }
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

        try{
            rideService.cancelRide(rideID, data);

            return new ResponseEntity<MessageResponseDTO>(
                    new MessageResponseDTO("Ride cancelled"),
                    HttpStatus.OK
            );
        }catch(RideCancellationException e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch(RideNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path="/{rideID}/stop",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StoppedRideResponseDTO> stopRide(
            @PathVariable Long rideID,
            @RequestBody StopRideRequestDTO data
    ){
        if(data == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        try{
            StoppedRideResponseDTO responseDTO = rideService.stopRide(rideID, data);
            return new ResponseEntity<StoppedRideResponseDTO>(
                    responseDTO,
                    HttpStatus.OK
            );
        }catch(RideNotFoundException e){
            return new ResponseEntity<StoppedRideResponseDTO>(HttpStatus.NOT_FOUND);
        }catch(StopRideException e){
            return new ResponseEntity<StoppedRideResponseDTO>(HttpStatus.BAD_REQUEST);
        } catch(Exception e){
            return new ResponseEntity<StoppedRideResponseDTO>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
       GetRideResponseDTO response = null;
       try {
           response = rideService.orderRide(userDetails.getUsername(), request);
       } catch (Exception e) {
           log.error("Exception u rideService.orderRide: ", e);
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
       }

       return ResponseEntity
               .status(HttpStatus.CREATED)
               .body(response);
   }

    @ExceptionHandler(NoAvailableDriverException.class)
    public ResponseEntity<MessageResponseDTO> handleNoDriver(NoAvailableDriverException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new MessageResponseDTO("Ride order declined: " + ex.getMessage()));
    }

    @PutMapping("/start")
    public ResponseEntity<GetRideResponseDTO> startActiveRide(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(
                rideService.startRide(userDetails.getUsername())
        );
    }



    @GetMapping(
            path = "/favorites",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<FavoriteRouteDTO>> getFavoriteRoutes(
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        List<FavoriteRoute> favorites =
                favoriteRouteService.getCurrentUserFavorites(
                        userDetails.getUsername()
                );

        List<FavoriteRouteDTO> response = favorites.stream()
                .map(routeMapper::favoriteRouteToDTO)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping(
            path = "/order/favorite",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GetRideResponseDTO> orderFromFavorite(
            @RequestBody OrderFromFavoriteRequestDTO request) {

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        GetRideResponseDTO rideResponse = favoriteRouteService.orderFavoriteRoute(userEmail, request);

        return ResponseEntity.ok(rideResponse);
    }



    @GetMapping("/active")
    public ResponseEntity<GetRideResponseDTO> getActiveRide(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if(userDetails == null) {
            System.out.println("UserDetails je null!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String driverEmail = userDetails.getUsername();
        System.out.println("Driver email: " + driverEmail);

        Ride ride = rideService.getActiveRideForDriver(driverEmail);
        if (ride == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rideService.mapToDTO(ride));
    }



}
