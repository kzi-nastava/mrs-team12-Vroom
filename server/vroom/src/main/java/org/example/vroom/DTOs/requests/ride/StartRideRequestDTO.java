package org.example.vroom.DTOs.requests.ride;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartRideRequestDTO {
    private LocalDateTime startTime;
}
