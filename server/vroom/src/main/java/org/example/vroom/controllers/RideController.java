package org.example.vroom.controllers;

import org.apache.coyote.Response;
import org.example.vroom.DTOs.requests.CancelRideDTO;
import org.example.vroom.DTOs.responses.MessageResponseDTO;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;


@RestController
@RequestMapping("/api/ride")
public class RideController {

    @PutMapping(
            path = "/cancel",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MessageResponseDTO> cancelRide(
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
}
