package org.example.vroom.mappers;

import org.example.vroom.DTOs.responses.route.GetRouteResponseDTO;
import org.example.vroom.DTOs.responses.route.PointResponseDTO;
import org.example.vroom.entities.Point;
import org.example.vroom.entities.Route;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

    public Route fromDTO(GetRouteResponseDTO dto) {
        if (dto == null) return null;

        Route route = new Route();
        route.setStartLocationLat(dto.getStartLocationLat());
        route.setStartLocationLng(dto.getStartLocationLng());
        route.setEndLocationLat(dto.getEndLocationLat());
        route.setEndLocationLng(dto.getEndLocationLng());

        List<Point> points;
        if (dto.getStops() != null) {
            points = dto.getStops().stream()
                    .map(p -> {
                        Point point = new Point();
                        point.setLat(p.getLat());
                        point.setLng(p.getLng());
                        return point;
                    })
                    .toList();
        } else {
            points = new ArrayList<>();
        }
        route.setStops(points);

        return route;
    }

    private List<Point> mapPointsFromDTO(List<PointResponseDTO> dtoPoints) {
        if (dtoPoints == null) return new ArrayList<>();

        List<Point> points = new ArrayList<>();
        for (PointResponseDTO dto : dtoPoints) {
            Point point = new Point();
            point.setLat(dto.getLat());
            point.setLng(dto.getLng());
            points.add(point);
        }
        return points;
    }
}
