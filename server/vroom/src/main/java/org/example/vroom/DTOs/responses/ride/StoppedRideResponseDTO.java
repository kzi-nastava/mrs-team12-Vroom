package org.example.vroom.DTOs.responses.ride;

import lombok.*;
import org.example.vroom.DTOs.responses.route.GetRouteResponseDTO;
import org.example.vroom.enums.RideStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoppedRideResponseDTO {
    private Long driverID;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private RideStatus status;
    private double price;
    private GetRouteResponseDTO route;
}
