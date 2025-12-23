package org.example.vroom.mappers;

import org.example.vroom.DTOs.PricelistDTO;
import org.example.vroom.entities.Pricelist;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PricelistMapper {

    public PricelistDTO toDTO(Pricelist pricelist) {
        if (pricelist == null)
            return null;

        return PricelistDTO.builder()
                .typePrice(pricelist.getTypePrice())
                .pricePerKm(pricelist.getPricePerKm())
                .build();
    }

    public Pricelist toEntity(PricelistDTO pricelistDTO) {
        if (pricelistDTO == null)
            return null;

        return Pricelist.builder()
                .typePrice(pricelistDTO.getTypePrice())
                .pricePerKm(pricelistDTO.getPricePerKm())
                .build();
    }

    public List<PricelistDTO> toDTOList(List<Pricelist> pricelists) {
        if (pricelists == null)
            return null;

        return pricelists.stream()
                .map(this::toDTO)
                .toList();
    }
}
