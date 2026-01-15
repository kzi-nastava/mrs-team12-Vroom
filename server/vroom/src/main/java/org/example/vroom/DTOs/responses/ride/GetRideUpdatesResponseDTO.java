package org.example.vroom.DTOs.responses.ride;

import lombok.*;
import org.example.vroom.DTOs.responses.route.PointResponseDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetRideUpdatesResponseDTO {
    double time;
    PointResponseDTO point;
}
