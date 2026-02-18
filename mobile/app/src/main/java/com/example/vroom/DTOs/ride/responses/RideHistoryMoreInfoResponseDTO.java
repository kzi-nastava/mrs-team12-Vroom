package com.example.vroom.DTOs.ride.responses;

import com.example.vroom.enums.RideStatus;

import java.util.List;

public class RideHistoryMoreInfoResponseDTO {
    private Long rideID;
    private List<String> passengers;
    private RideStatus status;
    private String cancelReason;
    private List<String> complaints;
    private Integer driverRating;
    private Integer vehicleRating;
    private String comment;

    public RideHistoryMoreInfoResponseDTO() {
    }

    public RideHistoryMoreInfoResponseDTO(Long rideID, List<String> passengers, RideStatus status,
                                          String cancelReason, List<String> complaints,
                                          Integer driverRating, Integer vehicleRating, String comment) {
        this.rideID = rideID;
        this.passengers = passengers;
        this.status = status;
        this.cancelReason = cancelReason;
        this.complaints = complaints;
        this.driverRating = driverRating;
        this.vehicleRating = vehicleRating;
        this.comment = comment;
    }

    public Long getRideID() {
        return rideID;
    }

    public void setRideID(Long rideID) {
        this.rideID = rideID;
    }

    public List<String> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<String> passengers) {
        this.passengers = passengers;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public List<String> getComplaints() {
        return complaints;
    }

    public void setComplaints(List<String> complaints) {
        this.complaints = complaints;
    }

    public Integer getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(Integer driverRating) {
        this.driverRating = driverRating;
    }

    public Integer getVehicleRating() {
        return vehicleRating;
    }

    public void setVehicleRating(Integer vehicleRating) {
        this.vehicleRating = vehicleRating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}