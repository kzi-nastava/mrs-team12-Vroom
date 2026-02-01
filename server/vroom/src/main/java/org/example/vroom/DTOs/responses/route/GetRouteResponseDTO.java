package org.example.vroom.DTOs.responses.route;

import jakarta.persistence.Column;
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
    String startAddress;
    String endAddress;
    List<PointResponseDTO> stops;
}
