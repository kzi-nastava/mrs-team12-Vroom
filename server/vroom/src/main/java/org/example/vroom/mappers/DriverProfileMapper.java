package org.example.vroom.mappers;

import java.sql.Driver;

import org.example.vroom.DTOs.DriverDTO;
import org.springframework.stereotype.Component;

@Component
public class DriverProfileMapper extends BaseProfileMapper {

    private final VehicleMapper vehicleMapper;

    public DriverProfileMapper(VehicleMapper vehicleMapper) {
        this.vehicleMapper = vehicleMapper;
    }

    public DriverDTO toDTO(Driver driver) {
        DriverDTO dto = DriverDTO.builder().build();
        mapBase(driver, dto);
        dto.setStatus(driver.getStatus());
        dto.setRatingCount(driver.getRatingCount());
        dto.setRatingSum(driver.getRatingSum());
        dto.setVehicle(vehicleMapper.toDTO(driver.getVehicle()));
        return dto;
    }

    public void updateEntity(Driver driver, DriverDTO dto) {
        driver.setFirstName(dto.getFirstName());
        driver.setLastName(dto.getLastName());
        driver.setPhoneNumber(dto.getPhoneNumber());
        driver.setAddress(dto.getAddress());
        driver.setProfilePhoto(dto.getProfilePhoto());
        // vehicle update ide posebnim endpointom
    }
}

