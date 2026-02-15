package org.example.vroom.DTOs.requests.ride;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StopRideRequestDTO {
    @PastOrPresent
    private LocalDateTime endTime;

    @NotNull
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private double stopLat;


    @NotNull
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private double stopLng;
}
