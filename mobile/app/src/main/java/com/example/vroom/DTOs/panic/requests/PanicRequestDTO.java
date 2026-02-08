package com.example.vroom.DTOs.panic.requests;

import java.time.LocalDateTime;

public class PanicRequestDTO {
    private Long rideId;
    private LocalDateTime activatedAt;

    public PanicRequestDTO(Long rideId, LocalDateTime activatedAt) {
        this.rideId = rideId;
        this.activatedAt = activatedAt;
    }

    public PanicRequestDTO() {
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public LocalDateTime getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(LocalDateTime activatedAt) {
        this.activatedAt = activatedAt;
    }
}
