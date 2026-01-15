package org.example.vroom.DTOs.requests.driver;

import lombok.Getter;
import lombok.Setter;
import org.example.vroom.enums.DriverStatus;

@Getter
@Setter
public class DriverUpdateRequestDTO {
    private DriverStatus status;
}
