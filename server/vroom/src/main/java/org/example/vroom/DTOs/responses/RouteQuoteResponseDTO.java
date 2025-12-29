package org.example.vroom.DTOs.responses;

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
