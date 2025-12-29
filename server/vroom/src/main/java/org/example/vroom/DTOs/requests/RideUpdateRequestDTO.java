package org.example.vroom.DTOs.requests;

import lombok.*;
import org.example.vroom.DTOs.responses.PointResponseDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RideUpdateRequestDTO {
    double time;
    PointResponseDTO point;
}
