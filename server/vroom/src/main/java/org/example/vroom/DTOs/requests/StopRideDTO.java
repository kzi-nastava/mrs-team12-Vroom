package org.example.vroom.DTOs.requests;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StopRideDTO {
    private LocalDateTime endTime;
    private double stopLat;
    private double stopLng;
}
