package org.example.vroom.mappers;

import org.example.vroom.DTOs.requests.driver.DriverRegisterRequestDTO;
import org.example.vroom.entities.Driver;
import org.example.vroom.entities.Vehicle;
import org.example.vroom.enums.DriverStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DriverRegisterMapper {
    @Autowired
    private final VehicleMapper vehicleMapper;

    public DriverRegisterMapper(VehicleMapper vehicleMapper) {
        this.vehicleMapper = vehicleMapper;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;
    public Driver toEntity(DriverRegisterRequestDTO dto, DriverStatus status, String password) {
        Driver driver = new Driver();

        driver.setEmail(dto.getEmail());
        driver.setFirstName(dto.getFirstName());
        driver.setLastName(dto.getLastName());
        driver.setPhoneNumber(dto.getPhoneNumber());
        driver.setAddress(dto.getAddress());
        driver.setGender(dto.getGender());
        driver.setStatus(status);
        driver.setPassword(password);

        Vehicle vehicle = vehicleMapper.toEntity(dto.getVehicle());
        driver.setVehicle(vehicle);

        return driver;
    }
}
