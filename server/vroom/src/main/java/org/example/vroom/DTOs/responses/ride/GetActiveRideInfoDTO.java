package org.example.vroom.DTOs.responses.ride;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetActiveRideInfoDTO {
    private Long rideId;
    private String startAddress;
    private String endAddress;
    private LocalDateTime startTime;
    private String driverName;
    private String creatorName;

}
