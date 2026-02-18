package com.example.vroom.DTOs.map;

import com.example.vroom.DTOs.route.responses.PointResponseDTO;

import java.util.List;

public class MapRouteDTO {
    private PointResponseDTO start;
    private PointResponseDTO end;

    private List<PointResponseDTO> stops;

    public MapRouteDTO(PointResponseDTO start, PointResponseDTO end, List<PointResponseDTO> stops) {
        this.start = start;
        this.end = end;
        this.stops = stops;
    }

    public MapRouteDTO() {
    }

    public PointResponseDTO getStart() {
        return start;
    }

    public void setStart(PointResponseDTO start) {
        this.start = start;
    }

    public PointResponseDTO getEnd() {
        return end;
    }

    public void setEnd(PointResponseDTO end) {
        this.end = end;
    }

    public List<PointResponseDTO> getStops() {
        return stops;
    }

    public void setStops(List<PointResponseDTO> stops) {
        this.stops = stops;
    }
}
