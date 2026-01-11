package org.example.vroom.services;

import org.example.vroom.DTOs.responses.AddressSuggestionResponseDTO;
import org.example.vroom.DTOs.responses.geocode.GeoapifyAddressResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class GeoService {
    @Value("${geoapify.api.key}")
    private String geoapifyApiKey;

    public List<AddressSuggestionResponseDTO> getLocations(String location, int limit) throws IOException {
        String text = URLEncoder.encode(location, StandardCharsets.UTF_8);
        URL url = new URL(
                "https://api.geoapify.com/v1/geocode/autocomplete?" +
                        "text=" + text +
                        "&limit=" + Integer.toString(limit) +
                        "&lang=sr" +
                        "&filter=countrycode:rs" +
                        "&apiKey=" + this.geoapifyApiKey
        );

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream is = con.getInputStream()) {
            GeoapifyAddressResponseDTO resp =
                    mapper.readValue(is, GeoapifyAddressResponseDTO.class);

            return resp.getFeatures().stream()
                    .map(f -> new AddressSuggestionResponseDTO(
                            f.getProperties().getFormatted(),
                            f.getProperties().getLat(),
                            f.getProperties().getLon()
                    ))
                    .toList();
        }
    }
}
