package org.example.vroom.DTOs;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.vroom.enums.DriverStatus;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DriverDTO  extends UserDTO {
    private DriverStatus status;
    private Long ratingCount;
    private Long ratingSum;
    private VehicleDTO vehicle;
}
