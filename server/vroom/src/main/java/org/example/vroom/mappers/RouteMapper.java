package org.example.vroom.mappers;

import org.example.vroom.DTOs.responses.GetRouteDTO;
import org.example.vroom.DTOs.responses.PointDTO;
import org.example.vroom.entities.Point;
import org.example.vroom.entities.Route;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouteMapper {
    public GetRouteDTO getRouteDTO(Route route) {
        return GetRouteDTO
                .builder()
                .startLocationLat(route.getStartLocationLat())
                .startLocationLng(route.getStartLocationLng())
                .endLocationLat(route.getEndLocationLat())
                .endLocationLng(route.getEndLocationLng())
                .stops(this.mapRoutePointsDTO(route.getStops()))
                .build();
    }

    public PointDTO routeStopToDTO(Point point) {
        if(point == null) return null;

        return PointDTO
                .builder()
                .lat(point.getLat())
                .lng(point.getLng())
                .build();
    }

    public List<PointDTO> mapRoutePointsDTO(List<Point> points) {
        if (points == null) {
            return null;
        }

        return points.stream()
                .map(this::routeStopToDTO)
                .toList();
    }
}
