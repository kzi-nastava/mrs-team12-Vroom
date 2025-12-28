package org.example.vroom.DTOs.responses;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetRouteResponseDTO {
    Double startLocationLat;
    Double startLocationLng;
    Double endLocationLat;
    Double endLocationLng;
    List<PointResponseDTO> stops;
}
