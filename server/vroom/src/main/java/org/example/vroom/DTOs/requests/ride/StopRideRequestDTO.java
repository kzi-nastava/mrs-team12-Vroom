package org.example.vroom.DTOs.requests.ride;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StopRideRequestDTO {
    private LocalDateTime endTime;
    private double stopLat;
    private double stopLng;
}
