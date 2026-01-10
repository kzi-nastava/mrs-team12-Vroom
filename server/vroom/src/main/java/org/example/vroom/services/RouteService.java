package org.example.vroom.services;

import org.example.vroom.DTOs.responses.GeoapifyRouteResponseDTO;
import org.example.vroom.DTOs.responses.RouteQuoteResponseDTO;
import org.example.vroom.entities.Pricelist;
import org.example.vroom.repositories.PriceListRepository;
import org.example.vroom.repositories.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class RouteService {
    @Value("${geoapify.api.key}")
    private String geoapifyApyKey;

    @Autowired
    private PriceListRepository priceListRepository;
    @Autowired
    private RouteRepository routeRepository;


    public RouteQuoteResponseDTO routeEstimation(String startLocation, String endLocation){
        String waypoints = startLocation+"|"+endLocation;
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

            ObjectMapper mapper = new ObjectMapper();
            GeoapifyRouteResponseDTO route = mapper.readValue(json, GeoapifyRouteResponseDTO.class);

            double distanceKm = (double)route.getFeatures().get(0).getProperties().getDistance() / 1000.0;
            double time = (double)route.getFeatures().get(0).getProperties().getTime() / 60.0;

            float pricePerKm = priceListRepository.getPricePerKm();
            double price = distanceKm * (double)pricePerKm;

            return new RouteQuoteResponseDTO(price, time);
        }catch(Exception e){
            return null;
        }
    }
}
