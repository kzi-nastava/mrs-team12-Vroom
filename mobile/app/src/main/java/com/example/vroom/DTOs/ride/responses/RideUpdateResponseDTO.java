package com.example.vroom.DTOs.ride.responses;

import com.example.vroom.DTOs.route.responses.PointResponseDTO;
import com.example.vroom.enums.RideStatus;

public class RideUpdateResponseDTO {
    Long driverID;
    PointResponseDTO currentLocation;
    Double timeLeft;
    RideStatus status;

    public RideUpdateResponseDTO() {
    }

    public RideUpdateResponseDTO(Long driverID, PointResponseDTO currentLocation, Double timeLeft, RideStatus status) {
        this.driverID = driverID;
        this.currentLocation = currentLocation;
        this.timeLeft = timeLeft;
        this.status = status;
    }

    public Long getDriverID() {
        return driverID;
    }

    public void setDriverID(Long driverID) {
        this.driverID = driverID;
    }

    public PointResponseDTO getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(PointResponseDTO currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Double getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(Double timeLeft) {
        this.timeLeft = timeLeft;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }
}
