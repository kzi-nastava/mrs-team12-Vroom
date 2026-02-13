package com.example.vroom.DTOs.ride.requests;

import com.example.vroom.DTOs.route.responses.GetRouteResponseDTO;
import com.example.vroom.enums.VehicleType;

import java.time.LocalDateTime;
import java.util.List;

public class RideRequestDTO {
    private List<String> locations;
    private List<String> passengersEmails;
    private VehicleType vehicleType;
    private Boolean babiesAllowed;
    private Boolean petsAllowed;
    private Boolean scheduled;
    private LocalDateTime scheduledTime;
    private GetRouteResponseDTO route;

    public RideRequestDTO() {
    }

    // Getters and Setters
    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public List<String> getPassengersEmails() {
        return passengersEmails;
    }

    public void setPassengersEmails(List<String> passengersEmails) {
        this.passengersEmails = passengersEmails;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Boolean getBabiesAllowed() {
        return babiesAllowed;
    }

    public void setBabiesAllowed(Boolean babiesAllowed) {
        this.babiesAllowed = babiesAllowed;
    }

    public Boolean getPetsAllowed() {
        return petsAllowed;
    }

    public void setPetsAllowed(Boolean petsAllowed) {
        this.petsAllowed = petsAllowed;
    }

    public Boolean getScheduled() {
        return scheduled;
    }

    public void setScheduled(Boolean scheduled) {
        this.scheduled = scheduled;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public GetRouteResponseDTO getRoute() {
        return route;
    }

    public void setRoute(GetRouteResponseDTO route) {
        this.route = route;
    }
}
