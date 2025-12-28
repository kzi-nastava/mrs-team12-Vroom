package org.example.vroom.mappers;

import org.example.vroom.DTOs.requests.DriverRegisterRequestDTO;
import org.example.vroom.entities.Driver;
import org.example.vroom.entities.Vehicle;
import org.example.vroom.enums.DriverStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DriverRegisterMapper {
    @Autowired
    private final VehicleMapper vehicleMapper;

    public DriverRegisterMapper(VehicleMapper vehicleMapper) {
        this.vehicleMapper = vehicleMapper;
    }

    public Driver toEntity(DriverRegisterRequestDTO dto) {
        Driver driver = new Driver();

        driver.setEmail(dto.getEmail());
        driver.setFirstName(dto.getFirstName());
        driver.setLastName(dto.getLastName());
        driver.setPhoneNumber(dto.getPhoneNumber());
        driver.setStatus(DriverStatus.INNACTIVE);

        Vehicle vehicle = vehicleMapper.toEntity(dto.getVehicle());
        driver.setVehicle(vehicle);

        return driver;
    }
}
