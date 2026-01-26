package org.example.vroom.DTOs.responses.ride;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.vroom.DTOs.responses.route.PointResponseDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RideUpdateResponseDTO {
    PointResponseDTO currentLocation;
    Double timeLeft;
}
