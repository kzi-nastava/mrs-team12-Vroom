package org.example.vroom.DTOs.responses.ride;

import lombok.*;
import org.example.vroom.enums.VehicleType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleRideResponseDTO {
    private String model;
    private VehicleType type;
    private Integer numberOfSeats;
    private Boolean babiesAllowed;
    private Boolean petsAllowed;

    @Builder.Default
    private double rating = 0.0;
}
