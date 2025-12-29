package org.example.vroom.controllers;

import org.example.vroom.DTOs.responses.RouteQuoteResponseDTO;
import org.example.vroom.DTOs.responses.MessageResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    @GetMapping(path="/quote")
    public ResponseEntity<RouteQuoteResponseDTO> getQuote(
            @RequestParam String startLocation,
            @RequestParam String endLocation
    ) {
        try{
            // call geoapify routing API in service layer to get km and time in order to calculate price
            return new ResponseEntity<RouteQuoteResponseDTO>(
                    new RouteQuoteResponseDTO(10.0, 10.0),
                    HttpStatus.OK
            );
        }catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RouteQuoteResponseDTO( 0, 0));
        }
    }
}
