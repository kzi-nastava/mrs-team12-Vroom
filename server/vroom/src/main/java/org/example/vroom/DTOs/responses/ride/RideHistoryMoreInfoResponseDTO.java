package org.example.vroom.DTOs.responses.ride;

import lombok.*;
import org.example.vroom.enums.RideStatus;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RideHistoryMoreInfoResponseDTO {
    private Long rideID;
    private List<String> passengers;
    private RideStatus status;
    private String cancelReason;
    private List<String> complaints;
    private Integer driverRating;
    private Integer vehicleRating;
    private String comment;
}
