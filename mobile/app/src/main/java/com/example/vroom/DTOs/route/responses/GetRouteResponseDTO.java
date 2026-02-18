package com.example.vroom.DTOs.route.responses;

import com.example.vroom.DTOs.admin.AdminUserDTO;

import java.util.List;

public class GetRouteResponseDTO {
    Double startLocationLat;
    Double startLocationLng;
    Double endLocationLat;
    Double endLocationLng;
    String startAddress;
    String endAddress;
    List<PointResponseDTO> stops;


    public GetRouteResponseDTO(Double startLocationLat, Double startLocationLng, Double endLocationLat, Double endLocationLng, String startAddress, String endAddress, List<PointResponseDTO> stops) {
        this.startLocationLat = startLocationLat;
        this.startLocationLng = startLocationLng;
        this.endLocationLat = endLocationLat;
        this.endLocationLng = endLocationLng;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.stops = stops;
    }

    public GetRouteResponseDTO() {
    }

    public Double getStartLocationLat() {
        return startLocationLat;
    }

    public void setStartLocationLat(Double startLocationLat) {
        this.startLocationLat = startLocationLat;
    }

    public Double getStartLocationLng() {
        return startLocationLng;
    }

    public void setStartLocationLng(Double startLocationLng) {
        this.startLocationLng = startLocationLng;
    }

    public Double getEndLocationLat() {
        return endLocationLat;
    }

    public void setEndLocationLat(Double endLocationLat) {
        this.endLocationLat = endLocationLat;
    }

    public Double getEndLocationLng() {
        return endLocationLng;
    }

    public void setEndLocationLng(Double endLocationLng) {
        this.endLocationLng = endLocationLng;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public List<PointResponseDTO> getStops() {
        return stops;
    }

    public void setStops(List<PointResponseDTO> stops) {
        this.stops = stops;
    }



}
