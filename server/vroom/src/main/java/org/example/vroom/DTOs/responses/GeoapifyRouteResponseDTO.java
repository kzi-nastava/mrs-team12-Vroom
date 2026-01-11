package org.example.vroom.DTOs.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoapifyRouteResponseDTO {
    private List<GeoapifyFeatureDTO> features;
}
