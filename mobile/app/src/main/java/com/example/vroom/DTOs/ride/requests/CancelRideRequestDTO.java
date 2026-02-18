package com.example.vroom.DTOs.ride.requests;

public class CancelRideRequestDTO {
    private String reason;

    public CancelRideRequestDTO(String reason) {
        this.reason = reason;
    }

    public CancelRideRequestDTO() {
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
