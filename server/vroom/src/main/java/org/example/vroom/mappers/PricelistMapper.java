package org.example.vroom.mappers;

import org.example.vroom.DTOs.PricelistDTO;
import org.example.vroom.entities.Pricelist;
import org.springframework.stereotype.Component;

@Component
public class PricelistMapper {

    public PricelistDTO toDTO(Pricelist pricelist) {
        if (pricelist == null)
            return null;

        return PricelistDTO.builder()
                .price_luxury(pricelist.getPrice_luxury())
                .price_standard(pricelist.getPrice_standard())
                .price_minivan(pricelist.getPrice_minivan())
                .build();
    }

    public Pricelist newPricelist(PricelistDTO pricelistDTO) {
        if (pricelistDTO == null)
            return null;

        return Pricelist.builder()
                .valid(true)
                .price_luxury(pricelistDTO.getPrice_luxury())
                .price_standard(pricelistDTO.getPrice_standard())
                .price_minivan(pricelistDTO.getPrice_minivan())
                .build();
    }

}
