package org.example.vroom.mappers;

import org.example.vroom.DTOs.responses.RoutePointDTO;
import org.example.vroom.entities.Point;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouteMapper {
    public RoutePointDTO routeStopToDTO(Point point) {
        if(point == null) return null;

        return RoutePointDTO
                .builder()
                .lat(point.getLat())
                .lng(point.getLng())
                .build();
    }

    public List<RoutePointDTO> mapRoutePointsDTO(List<Point> points) {
        if (points == null) {
            return null;
        }

        return points.stream()
                .map(this::routeStopToDTO)
                .toList();
    }
}
