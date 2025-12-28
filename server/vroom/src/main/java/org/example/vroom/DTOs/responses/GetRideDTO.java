package org.example.vroom.DTOs.responses;

import lombok.*;
import org.example.vroom.DTOs.DriverDTO;
import org.example.vroom.enums.RideStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetRideDTO {
    private DriverRideDTO driver;
    private GetRouteDTO route;

    private ArrayList<String> passengers;
    private ArrayList<String> complaints;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private RideStatus status;
    private double price;
    private boolean panicActivated;
    private Integer driverRating;
    private Integer vehicleRating;
}
