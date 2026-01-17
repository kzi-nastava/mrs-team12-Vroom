package org.example.vroom.controllers;

import org.example.vroom.DTOs.responses.geocode.AddressSuggestionResponseDTO;
import org.example.vroom.services.GeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/api/geo")
public class GeoController {
    @Autowired
    private GeoService geoService;

    @GetMapping("/autocomplete-address")
    public ResponseEntity<List<AddressSuggestionResponseDTO>> autocompleteAddress(@RequestParam String location) {
        try{
            List<AddressSuggestionResponseDTO> suggestions = geoService.getLocations(location, 5);
            return new ResponseEntity<List<AddressSuggestionResponseDTO>>(suggestions, HttpStatus.OK);
        }catch(IOException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/geocode-address")
    public ResponseEntity<AddressSuggestionResponseDTO> geocodeLocation(@RequestParam String location) {
        try{
            List<AddressSuggestionResponseDTO> suggestions = geoService.getLocations(location, 1);

            if (suggestions.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            AddressSuggestionResponseDTO best = suggestions.getFirst();
            return new ResponseEntity<AddressSuggestionResponseDTO>(best, HttpStatus.OK);

        }catch(IOException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
