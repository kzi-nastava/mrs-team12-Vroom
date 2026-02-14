package org.example.vroom.mappers;

import org.example.vroom.DTOs.requests.ride.FavoriteRouteResponseDTO;
import org.example.vroom.DTOs.responses.route.GetRouteResponseDTO;
import org.example.vroom.entities.FavoriteRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FavoriteRouteMapper {

    @Autowired
    private RouteMapper routeMapper;

    public FavoriteRouteResponseDTO toResponseDTO(FavoriteRoute favoriteRoute) {
        GetRouteResponseDTO routeDTO = routeMapper.getRouteDTO(favoriteRoute.getRoute());

        return FavoriteRouteResponseDTO.builder()
                .id(favoriteRoute.getId())
                .name(favoriteRoute.getName())
                .startAddress(favoriteRoute.getStartAddress())
                .endAddress(favoriteRoute.getEndAddress())
                .route(routeDTO)
                .build();
    }
}