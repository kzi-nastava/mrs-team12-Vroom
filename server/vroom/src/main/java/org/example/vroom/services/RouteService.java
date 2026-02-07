package org.example.vroom.services;

import jakarta.transaction.Transactional;
import org.example.vroom.DTOs.responses.geocode.GeoapifyRouteResponseDTO;
import org.example.vroom.DTOs.responses.route.PointResponseDTO;
import org.example.vroom.DTOs.responses.route.RouteQuoteResponseDTO;
import org.example.vroom.entities.Pricelist;
import org.example.vroom.repositories.PriceListRepository;
import org.example.vroom.repositories.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class RouteService {
    @Value("${geoapify.api.key}")
    private String geoapifyApyKey;

    @Autowired
    private PriceListRepository priceListRepository;
    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private ObjectMapper mapper;

    public String coordinatesToString(PointResponseDTO coordinates) {
        return coordinates.getLat() + "," + coordinates.getLng();
    }

//    @Cacheable(value = "route-estimation", key = "{#startLocation, #endLocation, #stopLocations}")
    public RouteQuoteResponseDTO routeEstimation(String startLocation, String endLocation, String stopLocations){
        String waypoints = startLocation;
        if(stopLocations != null && !stopLocations.isBlank()){
            List<String> stops = Arrays.asList(stopLocations.split(";"));
            for (String stop : stops) {
                waypoints += "|"+stop;
            }
        }
        waypoints += "|"+endLocation;
        try{
            URL url = new URL("https://api.geoapify.com/v1/routing?" +
                    "waypoints=" + waypoints +
                    "&mode=drive" +
                    "&traffic=approximated" +
                    "&max_speed=60" +
                    "&type=short" +
                    "&apiKey="+geoapifyApyKey
            );

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            InputStream is = conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream();

            StringBuilder response = new StringBuilder();
            try(BufferedReader br = new BufferedReader(new InputStreamReader(is))){
                String line;
                while((line = br.readLine()) != null){
                    response.append(line);
                }
            }
            conn.disconnect();

            String json = response.toString();
            System.out.println("Geoapify response: " + json);
            GeoapifyRouteResponseDTO route = mapper.readValue(json, GeoapifyRouteResponseDTO.class);

            double distanceKm = (double)route.getFeatures().get(0).getProperties().getDistance() / 1000.0;
            double time = (double)route.getFeatures().get(0).getProperties().getTime() / 60.0;


            Optional<Pricelist> pricelist = priceListRepository.findFirstByValidTrue();
            if(pricelist.isEmpty())
                throw new Exception("Pricelist is not defined");

            double pricePerKm = pricelist.get().getPriceStandard();
            double price = distanceKm * pricePerKm;

            return new RouteQuoteResponseDTO(price, time);
        }catch(Exception e){
            return null;
        }
    }

    public RouteQuoteResponseDTO routeEstimation(String startLocation, String endLocation) {
        String waypoints = startLocation + "|" + endLocation;
        try {
            URL url = new URL("https://api.geoapify.com/v1/routing?" +
                    "waypoints=" + waypoints +
                    "&mode=drive" +
                    "&traffic=approximated" +
                    "&max_speed=60" +
                    "&type=short" +
                    "&apiKey=" + geoapifyApyKey
            );

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            InputStream is = conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream();

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
            conn.disconnect();

            String json = response.toString();
            System.out.println("Geoapify response: " + json);
            GeoapifyRouteResponseDTO route = mapper.readValue(json, GeoapifyRouteResponseDTO.class);

            double distanceKm = (double) route.getFeatures().get(0).getProperties().getDistance() / 1000.0;
            double time = (double) route.getFeatures().get(0).getProperties().getTime() / 60.0;

            float pricePerKm = 5;
            double price = distanceKm * (double) pricePerKm;

            return new RouteQuoteResponseDTO(price, time);
        } catch (Exception e) {
            return null;
        }
    }
}
