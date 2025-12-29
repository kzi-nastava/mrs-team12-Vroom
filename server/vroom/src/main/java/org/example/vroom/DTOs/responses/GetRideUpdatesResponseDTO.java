package org.example.vroom.DTOs.responses;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetRideUpdatesResponseDTO {
    double time;
    PointResponseDTO point;
}
