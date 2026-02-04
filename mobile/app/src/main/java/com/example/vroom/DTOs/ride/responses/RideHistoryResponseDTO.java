package com.example.vroom.DTOs.ride.responses;

import com.example.vroom.enums.RideStatus;

import java.time.LocalDateTime;

public class RideHistoryResponseDTO {
    private Long rideId;
    private String startAddress;
    private String endAddress;
    private LocalDateTime startTime;
    private RideStatus status;
    private double price;
    private boolean panicActivated;

    public RideHistoryResponseDTO() {
    }

    public RideHistoryResponseDTO(Long rideId, String startAddress, String endAddress,
                                  LocalDateTime startTime, RideStatus status, double price,
                                  boolean panicActivated) {
        this.rideId = rideId;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.startTime = startTime;
        this.status = status;
        this.price = price;
        this.panicActivated = panicActivated;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
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

    public boolean isPanicActivated() {
        return panicActivated;
    }

    public void setPanicActivated(boolean panicActivated) {
        this.panicActivated = panicActivated;
    }
}
