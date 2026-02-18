package com.example.vroom.DTOs.panic.responses;

import java.time.LocalDateTime;

public class PanicNotificationResponseDTO {
    private Long id;
    private Long rideID;
    private String activatedBy;
    private LocalDateTime activatedAt;

    public PanicNotificationResponseDTO(Long id, Long rideID, String activatedBy, LocalDateTime activatedAt) {
        this.id = id;
        this.rideID = rideID;
        this.activatedBy = activatedBy;
        this.activatedAt = activatedAt;
    }

    public PanicNotificationResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRideID() {
        return rideID;
    }

    public void setRideID(Long rideID) {
        this.rideID = rideID;
    }

    public String getActivatedBy() {
        return activatedBy;
    }

    public void setActivatedBy(String activatedBy) {
        this.activatedBy = activatedBy;
    }

    public LocalDateTime getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(LocalDateTime activatedAt) {
        this.activatedAt = activatedAt;
    }
}
