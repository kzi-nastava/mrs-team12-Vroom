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
public class RideHistoryResponseDTO {
    private String startAddress;
    private String endAddress;
    private LocalDateTime startTime;
    private RideStatus status;
    private double price;
    private boolean panicActivated;
}
