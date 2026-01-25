package com.example.vroom.DTOs.driver.requests;

import com.example.vroom.enums.DriverStatus;

public class DriverChangeStatusRequestDTO {
    private DriverStatus status;

    public DriverChangeStatusRequestDTO(DriverStatus status) {
        this.status = status;
    }

    public DriverChangeStatusRequestDTO() {

    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }
}
