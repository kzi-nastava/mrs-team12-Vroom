package org.example.vroom.mappers;

import org.example.vroom.DTOs.requests.PricelistDTO;
import org.example.vroom.entities.Pricelist;
import org.springframework.stereotype.Component;

@Component
public class PricelistMapper {

    public PricelistDTO toDTO(Pricelist pricelist) {
        if (pricelist == null)
            return null;

        return PricelistDTO.builder()
                .priceLuxury(pricelist.getPriceLuxury())
                .priceStandard(pricelist.getPriceStandard())
                .priceMinivan(pricelist.getPriceMinivan())
                .build();
    }

    public Pricelist newPricelist(PricelistDTO pricelistDTO) {
        if (pricelistDTO == null)
            return null;

        return Pricelist.builder()
                .valid(true)
                .priceLuxury(pricelistDTO.getPriceLuxury())
                .priceStandard(pricelistDTO.getPriceStandard())
                .priceMinivan(pricelistDTO.getPriceMinivan())
                .build();
    }

}
