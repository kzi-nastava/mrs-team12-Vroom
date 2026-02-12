package com.example.vroom.DTOs.ride.responses;

import com.example.vroom.DTOs.route.responses.GetRouteResponseDTO;
import com.example.vroom.enums.RideStatus;

import java.time.LocalDateTime;
import java.util.List;

public class RideResponseDTO {
    private Long rideId;

    private String driverFirstName;
    private String driverLastName;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<String> passengers;
    private double price;
    private RideStatus status;
    private List<String> complaints;
    private Boolean panicActivated;
    private Integer driverRating;
    private Integer vehicleRating;
    private String comment;
    private String cancelReason;

    private GetRouteResponseDTO route;

    public RideResponseDTO(Long rideId, String driverFirstName, String driverLastName, LocalDateTime startTime, List<String> passengers, LocalDateTime endTime, double price, RideStatus status, List<String> complaints, Boolean panicActivated, Integer driverRating, Integer vehicleRating, String comment, String cancelReason, GetRouteResponseDTO route) {
        this.rideId = rideId;
        this.driverFirstName = driverFirstName;
        this.driverLastName = driverLastName;
        this.startTime = startTime;
        this.passengers = passengers;
        this.endTime = endTime;
        this.price = price;
        this.status = status;
        this.complaints = complaints;
        this.panicActivated = panicActivated;
        this.driverRating = driverRating;
        this.vehicleRating = vehicleRating;
        this.comment = comment;
        this.cancelReason = cancelReason;
        this.route = route;
    }

    public RideResponseDTO() {
    }


    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getDriverFirstName() {
        return driverFirstName;
    }

    public void setDriverFirstName(String driverFirstName) {
        this.driverFirstName = driverFirstName;
    }

    public String getDriverLastName() {
        return driverLastName;
    }

    public void setDriverLastName(String driverLastName) {
        this.driverLastName = driverLastName;
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

    public List<String> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<String> passengers) {
        this.passengers = passengers;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public List<String> getComplaints() {
        return complaints;
    }

    public void setComplaints(List<String> complaints) {
        this.complaints = complaints;
    }

    public Boolean getPanicActivated() {
        return panicActivated;
    }

    public void setPanicActivated(Boolean panicActivated) {
        this.panicActivated = panicActivated;
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

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public GetRouteResponseDTO getRoute() {
        return route;
    }

    public void setRoute(GetRouteResponseDTO route) {
        this.route = route;
    }
}
