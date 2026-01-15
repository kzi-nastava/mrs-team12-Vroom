package org.example.vroom.DTOs.responses.route;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteQuoteResponseDTO {
    private double price;
    private double time;
}
