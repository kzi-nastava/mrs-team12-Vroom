package org.example.vroom.mappers;

import org.example.vroom.DTOs.responses.PointDTO;
import org.example.vroom.entities.Point;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouteMapper {
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
