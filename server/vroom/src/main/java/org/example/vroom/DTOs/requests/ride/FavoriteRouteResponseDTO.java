package org.example.vroom.DTOs.requests.ride;

import lombok.*;
import org.example.vroom.DTOs.responses.route.GetRouteResponseDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteRouteResponseDTO {
    private Long id;
    private String name;
    private String startAddress;
    private String endAddress;
    private GetRouteResponseDTO route;
}