package com.example.vroom.DTOs.ride.responses;

import java.time.LocalDateTime;

public class GetActiveRideInfoDTO {
    private Long rideId;
    private String startAddress;
    private String endAddress;
    private LocalDateTime startTime;
    private String driverName;
    private String creatorName;
    private String profilePicture;
    public GetActiveRideInfoDTO() {
    }

    public GetActiveRideInfoDTO(Long rideId, String startAddress, String endAddress, LocalDateTime startTime, String driverName, String creatorName, String profilePicture) {
        this.rideId = rideId;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.startTime = startTime;
        this.driverName = driverName;
        this.creatorName = creatorName;
        this.profilePicture = profilePicture;
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

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}
