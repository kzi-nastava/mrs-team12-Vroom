package org.example.vroom.DTOs.requests;
import lombok.*;
import org.example.vroom.enums.VehicleType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleRequestDTO {

    private String model;
    private VehicleType type;
    private String licenceNumber;
    private Integer numberOfSeats;
    private Boolean babiesAllowed;
    private Boolean petsAllowed;
}
