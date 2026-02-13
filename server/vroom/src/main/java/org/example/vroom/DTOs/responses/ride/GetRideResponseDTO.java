package org.example.vroom.DTOs.responses.ride;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.example.vroom.DTOs.responses.route.GetRouteResponseDTO;
import org.example.vroom.DTOs.responses.driver.DriverRideResponseDTO;
import org.example.vroom.enums.RideStatus;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetRideResponseDTO {
    private Long rideID;
    private DriverRideResponseDTO driver;
    @NotNull
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
    @JsonProperty("isScheduled")
    private boolean isScheduled;
    private LocalDateTime scheduledTime;
}
