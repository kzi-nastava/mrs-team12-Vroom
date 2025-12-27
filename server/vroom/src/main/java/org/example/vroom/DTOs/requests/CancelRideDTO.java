package org.example.vroom.DTOs.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelRideDTO {
    private String type;
    private String reason;

    public CancelRideDTO(String type) {
        this.type = type;
    }
}
