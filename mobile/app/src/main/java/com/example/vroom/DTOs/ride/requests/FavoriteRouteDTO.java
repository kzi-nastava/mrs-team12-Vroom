package com.example.vroom.DTOs.ride.requests;

import com.example.vroom.enums.VehicleType;

public class FavoriteRouteDTO {
    private Long id;
    private String name;
    private String startAddress;
    private String endAddress;
    private Double startLocationLat;
    private Double startLocationLng;
    private Double endLocationLat;
    private Double endLocationLng;

    public FavoriteRouteDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}