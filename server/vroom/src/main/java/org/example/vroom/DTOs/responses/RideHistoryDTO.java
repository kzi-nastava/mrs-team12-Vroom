package org.example.vroom.DTOs.responses;

import lombok.*;
import org.example.vroom.enums.RideStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideHistoryDTO {
    private GetRouteDTO route;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private RideStatus status;
    private double price;
    private boolean panicActivated;
}
