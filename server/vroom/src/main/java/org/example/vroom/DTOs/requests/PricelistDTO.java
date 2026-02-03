package org.example.vroom.DTOs.requests;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricelistDTO {

    private double priceStandard;
    private double priceLuxury;
    private double priceMinivan;
}
