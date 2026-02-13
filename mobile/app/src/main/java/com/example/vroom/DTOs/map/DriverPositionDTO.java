package com.example.vroom.DTOs.map;

import com.example.vroom.DTOs.ride.responses.PointResponseDTO;
import com.example.vroom.enums.DriverStatus;

public class DriverPositionDTO {
    private Long driverId;
    private PointResponseDTO point;
    private DriverStatus status;

    public DriverPositionDTO(Long driverId, PointResponseDTO point, DriverStatus status) {
        this.driverId = driverId;
        this.point = point;
        this.status = status;
    }

    public DriverPositionDTO() {
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public PointResponseDTO getPoint() {
        return point;
    }

    public void setPoint(PointResponseDTO point) {
        this.point = point;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }
}