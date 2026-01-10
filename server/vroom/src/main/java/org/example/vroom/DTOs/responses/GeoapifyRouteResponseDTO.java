package org.example.vroom.DTOs.responses;

import lombok.Data;

import java.util.List;

@Data
public class GeoapifyRouteResponseDTO {
    private List<GeoapifyFeatureDTO> features;
}
