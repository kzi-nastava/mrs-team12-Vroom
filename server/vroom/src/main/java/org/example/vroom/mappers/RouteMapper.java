package org.example.vroom.mappers;

import org.example.vroom.DTOs.responses.GetRouteResponseDTO;
import org.example.vroom.DTOs.responses.PointResponseDTO;
import org.example.vroom.entities.Point;
import org.example.vroom.entities.Route;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouteMapper {
    public GetRouteResponseDTO getRouteDTO(Route route) {
        return GetRouteResponseDTO
                .builder()
                .startLocationLat(route.getStartLocationLat())
                .startLocationLng(route.getStartLocationLng())
                .endLocationLat(route.getEndLocationLat())
                .endLocationLng(route.getEndLocationLng())
                .stops(this.mapRoutePointsDTO(route.getStops()))
                .build();
    }

    public PointResponseDTO routeStopToDTO(Point point) {
        if(point == null) return null;

        return PointResponseDTO
                .builder()
                .lat(point.getLat())
                .lng(point.getLng())
                .build();
    }

    public List<PointResponseDTO> mapRoutePointsDTO(List<Point> points) {
        if (points == null) {
            return null;
        }

        return points.stream()
                .map(this::routeStopToDTO)
                .toList();
    }
}
