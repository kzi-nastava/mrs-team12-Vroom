package org.example.vroom.DTOs;

import lombok.*;
import org.example.vroom.enums.VehicleType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleDTO {
    private Long id;
    private String model;
    private VehicleType type;
    private String licenceNumber;
    private Integer numberOfSeats;
    private Boolean babiesAllowed;
    private Boolean petsAllowed;
    private Long ratingCount;
    private Long ratingSum;

}
