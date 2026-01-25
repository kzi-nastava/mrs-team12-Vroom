package org.example.vroom.controllers;

import org.example.vroom.DTOs.requests.panic.PanicRequestDTO;
import org.example.vroom.DTOs.responses.MessageResponseDTO;
import org.example.vroom.DTOs.responses.panic.PanicNotificationResponseDTO;
import org.example.vroom.entities.User;
import org.example.vroom.exceptions.panic.PanicNotificationNotFound;
import org.example.vroom.exceptions.ride.RideNotFoundException;
import org.example.vroom.exceptions.user.UserNotFoundException;
import org.example.vroom.services.PanicNotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping(path = "/api/panics")
public class PanicNotificationsController {
    @Autowired
    private PanicNotificationsService panicNotificationsService;

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

    @GetMapping("/{panicID}")
    public ResponseEntity<PanicNotificationResponseDTO> getPanicNotifications(@PathVariable Long panicID) {
        try{
           PanicNotificationResponseDTO  notification = panicNotificationsService.getPanic(panicID);

            return new ResponseEntity<PanicNotificationResponseDTO>(notification, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @MessageMapping("panic")
    @SendTo("/socket-publisher/panic-notifications")
    public MessageResponseDTO panicRide(
            Principal principal,
            @Payload PanicRequestDTO data
    ){

        if(data == null || data.getRideId() == null)  return new MessageResponseDTO("Invalid panic request data");

        try{
            panicNotificationsService.activatePanic(data, principal.getName());

            return new MessageResponseDTO("Administrators are notified, please hang in there while they resolve the issue");
        }catch(RideNotFoundException | UserNotFoundException  e){
            return new MessageResponseDTO("User or ride not found, please try again");
        }catch(Exception e){
            return new MessageResponseDTO("System error occured");
        }
    }


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
