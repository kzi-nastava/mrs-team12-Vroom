package org.example.vroom.DTOs;

import org.example.vroom.DTOs.responses.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.vroom.DTOs.responses.GetRouteResponseDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteRouteDTO {
    private Long id;
    private String name;
    private GetRouteResponseDTO route;
}
