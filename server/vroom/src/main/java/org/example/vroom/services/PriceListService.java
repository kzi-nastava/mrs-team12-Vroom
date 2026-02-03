package org.example.vroom.services;

import org.example.vroom.DTOs.PricelistDTO;
import org.example.vroom.entities.Pricelist;
import org.example.vroom.enums.VehicleType;
import org.example.vroom.exceptions.pricelist.InvalidVehicleTypeException;
import org.example.vroom.exceptions.pricelist.NoValidPricelistException;
import org.example.vroom.mappers.PricelistMapper;
import org.example.vroom.repositories.PriceListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PriceListService {

    @Autowired
    private PriceListRepository priceListRepository;

    @Autowired
    private PricelistMapper priceListMapper;

    public void setNewPriceList(PricelistDTO pricelistDTO) {
        Optional<Pricelist> pricelistOptional = priceListRepository.findFirstByValidTrue();
        if (pricelistOptional.isPresent()) {
            Pricelist pricelist = pricelistOptional.get();
            pricelist.setValid(false);
            priceListRepository.save(pricelist);
        }
        Pricelist newPricelist = priceListMapper.newPricelist(pricelistDTO);
        priceListRepository.save(newPricelist);
    }

    public double getPricePerType(VehicleType type){
        Optional<Pricelist> pricelistOptional = priceListRepository.findFirstByValidTrue();
        if (pricelistOptional.isEmpty()) {
            throw new NoValidPricelistException("Admin hasn't defined a valid pricelist");
        }
        Pricelist pricelist = pricelistOptional.get();
        switch (type){
            case STANDARD -> {
                return pricelist.getPrice_standard();
            }
            case LUXURY -> {
                return pricelist.getPrice_luxury();
            }
            case MINIVAN -> {
                return pricelist.getPrice_minivan();
            }
            case null, default -> {
                throw new InvalidVehicleTypeException("Invalid vehicle type");
            }
        }
    }
}
