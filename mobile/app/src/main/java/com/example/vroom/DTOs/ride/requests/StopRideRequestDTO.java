package com.example.vroom.DTOs.ride.requests;

import java.time.LocalDateTime;

public class StopRideRequestDTO {
    private LocalDateTime endTime;
    private double stopLat;
    private double stopLng;

    public StopRideRequestDTO(LocalDateTime endTime, double stopLat, double stopLng) {
        this.endTime = endTime;
        this.stopLat = stopLat;
        this.stopLng = stopLng;
    }

    public StopRideRequestDTO() {
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public double getStopLat() {
        return stopLat;
    }

    public void setStopLat(double stopLat) {
        this.stopLat = stopLat;
    }

    public double getStopLng() {
        return stopLng;
    }

    public void setStopLng(double stopLng) {
        this.stopLng = stopLng;
    }
}
