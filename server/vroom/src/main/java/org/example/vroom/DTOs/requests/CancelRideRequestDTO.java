package org.example.vroom.DTOs.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelRideRequestDTO {
    private String type;
    private String reason;

    public CancelRideRequestDTO(String type) {
        this.type = type;
    }
}
