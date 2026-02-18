package com.example.vroom.DTOs.route.responses;

public class PointResponseDTO {
    private Double lat;
    private Double lng;

    public PointResponseDTO(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public PointResponseDTO() {
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
