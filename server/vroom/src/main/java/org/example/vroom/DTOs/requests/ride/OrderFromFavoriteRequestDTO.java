package org.example.vroom.DTOs.requests.ride;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderFromFavoriteRequestDTO {

    private Long favoriteRouteId;
    private Boolean scheduled;
}