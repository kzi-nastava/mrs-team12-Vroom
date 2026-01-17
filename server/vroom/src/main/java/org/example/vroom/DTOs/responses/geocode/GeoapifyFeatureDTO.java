package org.example.vroom.DTOs.responses.geocode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoapifyFeatureDTO {
    private GeoapifyPropertiesDTO properties;
}
