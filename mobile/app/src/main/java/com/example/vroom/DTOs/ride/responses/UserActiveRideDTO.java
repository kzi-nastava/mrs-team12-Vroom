package com.example.vroom.DTOs.ride.responses;

import com.example.vroom.DTOs.route.responses.GetRouteResponseDTO;
import com.example.vroom.enums.RideStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class UserActiveRideDTO {
    private Long rideID;
    private String driverName;
    private String vehicleInfo;
    private GetRouteResponseDTO route;
    private List<String> passengers;
    private LocalDateTime scheduledTime;
    private RideStatus status;
    private double price;
    private boolean isScheduled;

    public UserActiveRideDTO() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserActiveRideDTO that = (UserActiveRideDTO) o;
        return Double.compare(price, that.price) == 0 && isScheduled == that.isScheduled && Objects.equals(rideID, that.rideID) && Objects.equals(driverName, that.driverName) && Objects.equals(vehicleInfo, that.vehicleInfo) && Objects.equals(route, that.route) && Objects.equals(passengers, that.passengers) && Objects.equals(scheduledTime, that.scheduledTime) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rideID, driverName, vehicleInfo, route, passengers, scheduledTime, status, price, isScheduled);
    }

    public UserActiveRideDTO(Long rideID, String driverName, String vehicleInfo, GetRouteResponseDTO route, List<String> passengers, LocalDateTime scheduledTime, RideStatus status, double price, boolean isScheduled) {
        this.rideID = rideID;
        this.driverName = driverName;
        this.vehicleInfo = vehicleInfo;
        this.route = route;
        this.passengers = passengers;
        this.scheduledTime = scheduledTime;
        this.status = status;
        this.price = price;
        this.isScheduled = isScheduled;
    }

    public Long getRideID() {
        return rideID;
    }

    public void setRideID(Long rideID) {
        this.rideID = rideID;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getVehicleInfo() {
        return vehicleInfo;
    }

    public void setVehicleInfo(String vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }

    public GetRouteResponseDTO getRoute() {
        return route;
    }

    public void setRoute(GetRouteResponseDTO route) {
        this.route = route;
    }

    public List<String> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<String> passengers) {
        this.passengers = passengers;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
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

    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }
}
