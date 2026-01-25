package org.example.vroom.DTOs.responses.ride;

import lombok.*;
import org.example.vroom.DTOs.responses.route.GetRouteResponseDTO;
import org.example.vroom.DTOs.responses.driver.DriverRideResponseDTO;
import org.example.vroom.enums.RideStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetRideResponseDTO {
    private DriverRideResponseDTO driver;
    private GetRouteResponseDTO route;

    private List<String> passengers;
    private List<String> complaints;


    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private RideStatus status;
    private double price;
    private boolean panicActivated;
    private Integer driverRating;
    private Integer vehicleRating;

    private LocalDateTime scheduledTime;
}
