package org.example.vroom.DTOs.responses;

import lombok.*;
import org.example.vroom.enums.Gender;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverRideResponseDTO {
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;

    @Builder.Default
    private double rating = 0.0;

    private VehicleRideResponseDTO vehicle;
}
