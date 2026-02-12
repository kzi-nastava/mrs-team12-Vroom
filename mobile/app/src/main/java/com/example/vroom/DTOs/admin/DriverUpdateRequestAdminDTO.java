package com.example.vroom.DTOs.admin;

import com.example.vroom.DTOs.driver.requests.DriverDTO;
import com.example.vroom.enums.RequestStatus;
import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;

public class DriverUpdateRequestAdminDTO {

    @SerializedName("id")
    private Long id;

    @SerializedName("driverId")
    private Long driverId;

    @SerializedName("status")
    private RequestStatus status;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("payload")
    private DriverDTO payload;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public DriverDTO getPayload() { return payload; }
    public void setPayload(DriverDTO payload) { this.payload = payload; }
}