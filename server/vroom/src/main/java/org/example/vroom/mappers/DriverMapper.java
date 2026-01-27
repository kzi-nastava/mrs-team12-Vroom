package org.example.vroom.mappers;

import org.example.vroom.DTOs.DriverDTO;
import org.example.vroom.DTOs.requests.driver.DriverRegistrationRequestDTO;
import org.example.vroom.DTOs.responses.driver.DriverRideResponseDTO;
import org.example.vroom.entities.Driver;
import org.example.vroom.entities.Vehicle;
import org.example.vroom.enums.DriverStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DriverMapper {

    private final VehicleMapper vehicleMapper;

    public DriverMapper(VehicleMapper vehicleMapper) {
        this.vehicleMapper = vehicleMapper;
    }

    public DriverDTO toDTO(Driver driver){
        if (driver == null) {
            return null;
        }

        return DriverDTO.builder()
                .firstName(driver.getFirstName())
                .lastName(driver.getLastName())
                .email(driver.getEmail())
                .gender(driver.getGender())
                .phoneNumber(driver.getPhoneNumber())
                .address(driver.getAddress())
                .profilePhoto(driver.getProfilePhoto())
                .blockedReason(driver.getBlockedReason())
                .status(driver.getStatus())
                .ratingCount(driver.getRatingCount())
                .ratingSum(driver.getRatingSum())
                .vehicle(vehicleMapper.toDTO(driver.getVehicle()))
                .build();
    }

    public Driver toEntity(DriverDTO driverDTO, String password){
        if (driverDTO == null) {
            return null;
        }

        return Driver.builder()
                .password(password)
                .firstName(driverDTO.getFirstName())
                .lastName(driverDTO.getLastName())
                .email(driverDTO.getEmail())
                .gender(driverDTO.getGender())
                .phoneNumber(driverDTO.getPhoneNumber())
                .address(driverDTO.getAddress())
                .profilePhoto(driverDTO.getProfilePhoto())
                .blockedReason(driverDTO.getBlockedReason())
                .status(driverDTO.getStatus())
                .ratingCount(driverDTO.getRatingCount())
                .ratingSum(driverDTO.getRatingSum())
                .vehicle(vehicleMapper.toEntity(driverDTO.getVehicle()))
                .build();

    }

    public DriverRideResponseDTO toDriverRideDTO(Driver driver){
        if(driver == null) return null;

        return DriverRideResponseDTO
                .builder()
                .firstName(driver.getFirstName())
                .lastName(driver.getLastName())
                .email(driver.getEmail())
                .gender(driver.getGender())
                .rating(driver.getRating())
                .vehicle(vehicleMapper.toVehicleRideDTO(driver.getVehicle()))
                .build();
    }


    public List<DriverDTO> toDTOList(List<Driver> drivers){
        if (drivers == null) {
            return null;
        }

        return drivers.stream()
                .map(this::toDTO)
                .toList();
    }

    public Driver toEntity(DriverRegistrationRequestDTO dto, String encodedPassword) {

        Vehicle vehicle = Vehicle.builder()
                .brand(dto.getBrand())
                .model(dto.getModel())
                .type(dto.getType())
                .licenceNumber(dto.getLicenceNumber())
                .numberOfSeats(dto.getNumberOfSeats())
                .babiesAllowed(dto.getBabiesAllowed())
                .petsAllowed(dto.getPetsAllowed())
                .build();

        return Driver.builder()
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .address(dto.getAddress())
                .gender(dto.getGender())
                .phoneNumber(dto.getPhoneNumber())
                .profilePhoto(dto.getProfilePhoto())
                .password(encodedPassword)
                .status(DriverStatus.AVAILABLE)
                .blockedReason(null)
                .vehicle(vehicle)
                .ratingCount(0L)
                .ratingSum(0L)
                .build();
    }


}
