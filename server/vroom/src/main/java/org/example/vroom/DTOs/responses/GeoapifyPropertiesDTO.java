package org.example.vroom.DTOs.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoapifyPropertiesDTO {
    private double distance;
    private int time;
}
