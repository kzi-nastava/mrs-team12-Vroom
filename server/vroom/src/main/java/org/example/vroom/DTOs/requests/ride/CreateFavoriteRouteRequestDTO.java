package org.example.vroom.DTOs.requests.ride;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateFavoriteRouteRequestDTO {
    private Long rideId;
    private String name;
}