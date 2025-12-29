package org.example.vroom.DTOs.responses;

import lombok.*;
import org.example.vroom.enums.DriverStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverPositionDTO {
    private int driverId;
    private PointResponseDTO point;
    private DriverStatus status;
}
