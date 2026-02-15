package org.example.vroom.DTOs.requests.ride;

import jakarta.annotation.Nullable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelRideRequestDTO {
    private String reason;
}
