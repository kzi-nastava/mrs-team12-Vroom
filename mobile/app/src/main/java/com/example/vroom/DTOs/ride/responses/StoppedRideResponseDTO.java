package com.example.vroom.DTOs.ride.responses;

import com.example.vroom.DTOs.route.responses.GetRouteResponseDTO;
import com.example.vroom.enums.RideStatus;

import java.time.LocalDateTime;

public class StoppedRideResponseDTO {
    private Long driverID;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private RideStatus status;
    private double price;
    private GetRouteResponseDTO route;

    public StoppedRideResponseDTO(Long driverID, LocalDateTime startTime, LocalDateTime endTime, RideStatus status, double price, GetRouteResponseDTO route) {
        this.driverID = driverID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.price = price;
        this.route = route;
    }

    public StoppedRideResponseDTO() {
    }

    public Long getDriverID() {
        return driverID;
    }

    public void setDriverID(Long driverID) {
        this.driverID = driverID;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public GetRouteResponseDTO getRoute() {
        return route;
    }

    public void setRoute(GetRouteResponseDTO route) {
        this.route = route;
    }
}
