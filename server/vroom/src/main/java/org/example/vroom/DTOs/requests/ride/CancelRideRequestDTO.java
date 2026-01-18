package org.example.vroom.DTOs.requests.ride;

import jakarta.annotation.Nullable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelRideRequestDTO {
    private String type;
    @Nullable
    private String reason;

    public CancelRideRequestDTO(String type) {
        this.type = type;
    }
}
