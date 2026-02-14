package org.example.vroom.DTOs.responses.ride;

import lombok.*;
import org.example.vroom.DTOs.responses.route.GetRouteResponseDTO;
import org.example.vroom.enums.RideStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserActiveRideDTO {
    private Long rideID;
    private String driverName;
    private String vehicleInfo;
    private GetRouteResponseDTO route;
    private List<String> passengers;
    private LocalDateTime scheduledTime;
    private RideStatus status;
    private double price;
    private boolean isScheduled;
}
