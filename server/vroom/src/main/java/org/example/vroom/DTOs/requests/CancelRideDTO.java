package org.example.vroom.DTOs.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelRideDTO {
    private Long rideID;
    private String type;
    private String reason;

    public CancelRideDTO(Long rideID, String type) {
        this.rideID = rideID;
        this.type = type;
    }
}
