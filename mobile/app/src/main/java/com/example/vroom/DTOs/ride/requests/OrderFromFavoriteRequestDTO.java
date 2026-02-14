package com.example.vroom.DTOs.ride.requests;


import com.example.vroom.enums.VehicleType;

import java.time.LocalDateTime;

public class OrderFromFavoriteRequestDTO {
    private Long favoriteRouteId;
    private VehicleType vehicleType;
    private Boolean babiesAllowed;
    private Boolean petsAllowed;
    private LocalDateTime scheduledTime;

    public OrderFromFavoriteRequestDTO() {
    }

    public OrderFromFavoriteRequestDTO(Long favoriteRouteId, VehicleType vehicleType,
                                       Boolean babiesAllowed, Boolean petsAllowed,
                                       LocalDateTime scheduledTime) {
        this.favoriteRouteId = favoriteRouteId;
        this.vehicleType = vehicleType;
        this.babiesAllowed = babiesAllowed;
        this.petsAllowed = petsAllowed;
        this.scheduledTime = scheduledTime;
    }


    public Long getFavoriteRouteId() {
        return favoriteRouteId;
    }

    public void setFavoriteRouteId(Long favoriteRouteId) {
        this.favoriteRouteId = favoriteRouteId;
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

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
}