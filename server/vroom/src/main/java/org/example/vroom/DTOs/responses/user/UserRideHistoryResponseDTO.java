package org.example.vroom.DTOs.responses.user;

import lombok.*;
import org.example.vroom.DTOs.responses.route.GetRouteResponseDTO;
import org.example.vroom.enums.RideStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRideHistoryResponseDTO {
    private Long rideId;

    private String driverFirstName;
    private String driverLastName;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<String> passengers;
    private double price;
    private RideStatus status;
    private List<String> complaints;
    private Boolean panicActivated;
    private Integer driverRating;
    private Integer vehicleRating;
    private String comment;
    private String cancelReason;

    private GetRouteResponseDTO route;
}
