package org.example.vroom.DTOs.responses.ride;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.vroom.DTOs.responses.route.PointResponseDTO;
import org.example.vroom.enums.RideStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RideUpdateResponseDTO {
    Long driverID;
    PointResponseDTO currentLocation;
    Double timeLeft;
    RideStatus status;
}
