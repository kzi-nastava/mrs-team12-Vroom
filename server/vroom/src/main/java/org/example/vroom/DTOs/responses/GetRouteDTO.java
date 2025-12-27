package org.example.vroom.DTOs.responses;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetRouteDTO {
    Double startLocationLat;
    Double startLocationLng;
    Double endLocationLat;
    Double endLocationLng;
    List<PointDTO> stops;
}
