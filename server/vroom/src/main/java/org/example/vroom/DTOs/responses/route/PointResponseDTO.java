package org.example.vroom.DTOs.responses.route;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointResponseDTO {
    private Double lat;
    private Double lng;
}
