package org.example.vroom.controllers;

import org.example.vroom.DTOs.requests.panic.PanicRequestDTO;
import org.example.vroom.DTOs.responses.MessageResponseDTO;
import org.example.vroom.DTOs.responses.panic.PanicNotificationResponseDTO;
import org.example.vroom.entities.User;
import org.example.vroom.exceptions.panic.IllegalRideException;
import org.example.vroom.exceptions.panic.PanicNotificationNotFound;
import org.example.vroom.exceptions.ride.RideNotFoundException;
import org.example.vroom.exceptions.user.UserNotFoundException;
import org.example.vroom.services.PanicNotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(path = "/api/panics")
public class PanicNotificationsController {
    @Autowired
    private PanicNotificationsService panicNotificationsService;

   // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<List<PanicNotificationResponseDTO>> getPanicNotifications(
            @RequestParam boolean active
    ) {
        try{
            List<PanicNotificationResponseDTO>  notifications = panicNotificationsService.getPanics(active);

            return new ResponseEntity<List<PanicNotificationResponseDTO>>(notifications, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{panicID}")
    public ResponseEntity<PanicNotificationResponseDTO> getPanicNotifications(@PathVariable Long panicID) {
        try{
           PanicNotificationResponseDTO notification = panicNotificationsService.getPanic(panicID);

            return new ResponseEntity<PanicNotificationResponseDTO>(notification, HttpStatus.OK);
        }catch(PanicNotificationNotFound e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping
    public ResponseEntity<MessageResponseDTO> createPanic(
            @AuthenticationPrincipal User user,
            @RequestBody PanicRequestDTO data
    ){
        if(data == null || data.getRideId() == null)
            return new ResponseEntity<MessageResponseDTO> (
                new MessageResponseDTO("Invalid panic request data"),
                HttpStatus.NO_CONTENT
            );

        try{
            panicNotificationsService.createPanicNotification(data, user.getId());

            return new ResponseEntity<MessageResponseDTO> (
                    new MessageResponseDTO("Administrators are notified, please hang in there while they resolve the issue"),
                    HttpStatus.CREATED
            );
        }catch(RideNotFoundException | UserNotFoundException  e){
            return new ResponseEntity<MessageResponseDTO> (
                    new MessageResponseDTO("User or ride not found, please try again"),
                    HttpStatus.NOT_FOUND
            );
        }catch(IllegalRideException e){
            return new ResponseEntity<MessageResponseDTO> (
                    new MessageResponseDTO("Ride is not active, cannot call PANIC"),
                    HttpStatus.BAD_REQUEST
            );
        }catch(Exception e){
            return new ResponseEntity<MessageResponseDTO> (
                new MessageResponseDTO("System error ocurred"),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{panicID}/resolve")
    public ResponseEntity<MessageResponseDTO> resolvePanic(
        @PathVariable Long panicID
    ){
        try{
            panicNotificationsService.resolvePanic(panicID);

            return new ResponseEntity<MessageResponseDTO>(
                    new MessageResponseDTO("Resolved PANIC"),
                    HttpStatus.OK
            );
        }catch(PanicNotificationNotFound e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
