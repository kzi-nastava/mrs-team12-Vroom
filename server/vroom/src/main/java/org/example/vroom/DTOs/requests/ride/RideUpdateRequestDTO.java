package org.example.vroom.DTOs.requests.ride;

import lombok.*;
import org.example.vroom.DTOs.responses.route.PointResponseDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RideUpdateRequestDTO {
    double time;
    PointResponseDTO point;
}
