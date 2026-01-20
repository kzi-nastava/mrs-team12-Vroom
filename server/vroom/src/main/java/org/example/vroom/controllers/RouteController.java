package org.example.vroom.controllers;

import org.example.vroom.DTOs.responses.route.RouteQuoteResponseDTO;
import org.example.vroom.services.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/routes")
public class RouteController {
    @Autowired
    private RouteService routeService;

    @GetMapping(path="/quote")
    public ResponseEntity<RouteQuoteResponseDTO> getQuote(
            @RequestParam String startLocation,
            @RequestParam String endLocation,
            @RequestParam(required = false) String stops
            ) {
        // call geoapify routing API in service layer to get km and time in order to calculate price
        try{
            RouteQuoteResponseDTO res = routeService.routeEstimation(startLocation, endLocation, stops);
            return new ResponseEntity<RouteQuoteResponseDTO>(
                    res,
                    HttpStatus.OK
            );
        }catch(Exception e){
            return new ResponseEntity<RouteQuoteResponseDTO>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/osrm-route")
    public ResponseEntity<Object> getOsrmRoute(@RequestParam String coords) {
        String url = "https://router.project-osrm.org/route/v1/driving/"
                + coords + "?overview=full&geometries=geojson";

        RestTemplate rest = new RestTemplate();
        Map<String, Object> response = rest.getForObject(url, Map.class);

        return ResponseEntity.ok(response);
    }

}
