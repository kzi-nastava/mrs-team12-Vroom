package com.example.vroom.DTOs.ride.responses;

import com.example.vroom.DTOs.driver.requests.DriverRideResponseDTO;
import com.example.vroom.DTOs.route.responses.GetRouteResponseDTO;
import com.example.vroom.enums.RideStatus;
import com.google.gson.annotations.SerializedName;



import java.time.LocalDateTime;
import java.util.List;

public class GetRideResponseDTO {
    private Long rideID;
    private DriverRideResponseDTO driver;
    private GetRouteResponseDTO route;
    private List<String> passengers;
    private List<String> complaints;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private RideStatus status;
    private double price;
    private boolean panicActivated;
    private Integer driverRating;
    private Integer vehicleRating;

    @SerializedName("isScheduled")
    private boolean isScheduled;
    private LocalDateTime scheduledTime;

    // Getters and Setters
    public Long getRideID() { return rideID; }
    public void setRideID(Long rideID) { this.rideID = rideID; }

    public DriverRideResponseDTO getDriver() { return driver; }
    public void setDriver(DriverRideResponseDTO driver) { this.driver = driver; }

    public GetRouteResponseDTO getRoute() { return route; }
    public void setRoute(GetRouteResponseDTO route) { this.route = route; }

    public List<String> getPassengers() { return passengers; }
    public void setPassengers(List<String> passengers) { this.passengers = passengers; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public RideStatus getStatus() { return status; }
    public void setStatus(RideStatus status) { this.status = status; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isPanicActivated() { return panicActivated; }
    public void setPanicActivated(boolean panicActivated) { this.panicActivated = panicActivated; }

    public Integer getDriverRating() { return driverRating; }
    public void setDriverRating(Integer driverRating) { this.driverRating = driverRating; }

    public Integer getVehicleRating() { return vehicleRating; }
    public void setVehicleRating(Integer vehicleRating) { this.vehicleRating = vehicleRating; }

    public boolean isScheduled() { return isScheduled; }
    public void setScheduled(boolean scheduled) { isScheduled = scheduled; }

    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }

    public List<String> getComplaints() { return complaints; }
    public void setComplaints(List<String> complaints) { this.complaints = complaints; }
}