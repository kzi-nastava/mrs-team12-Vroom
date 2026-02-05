package org.example.vroom.DTOs.requests.ride;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StopRideRequestDTO {
    @org.jetbrains.annotations.NotNull
    private LocalDateTime endTime;
    @NotNull
    private double stopLat;
    @NotNull
    private double stopLng;
}
