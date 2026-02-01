package org.example.vroom.mappers;

import org.example.vroom.DTOs.VehicleDTO;
import org.example.vroom.DTOs.requests.vehicle.VehicleRequestDTO;
import org.example.vroom.DTOs.responses.ride.VehicleRideResponseDTO;
import org.example.vroom.entities.Vehicle;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VehicleMapper {

    public VehicleDTO toDTO(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }

        return VehicleDTO.builder()
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .type(vehicle.getType())
                .licenceNumber(vehicle.getLicenceNumber())
                .numberOfSeats(vehicle.getNumberOfSeats())
                .babiesAllowed(vehicle.getBabiesAllowed())
                .petsAllowed(vehicle.getPetsAllowed())
                .ratingCount(vehicle.getRatingCount())
                .ratingSum(vehicle.getRatingSum())
                .build();
    }

    public Vehicle toEntity(VehicleDTO vehicleDTO) {
        if (vehicleDTO == null) {
            return null;
        }

        return Vehicle.builder()
                .brand(vehicleDTO.getBrand())
                .model(vehicleDTO.getModel())
                .type(vehicleDTO.getType())
                .licenceNumber(vehicleDTO.getLicenceNumber())
                .numberOfSeats(vehicleDTO.getNumberOfSeats())
                .babiesAllowed(vehicleDTO.getBabiesAllowed())
                .petsAllowed(vehicleDTO.getPetsAllowed())
                .ratingCount(vehicleDTO.getRatingCount())
                .ratingSum(vehicleDTO.getRatingSum())
                .build();
    }

    public VehicleRideResponseDTO toVehicleRideDTO(Vehicle vehicle) {
        if(vehicle == null) return null;

        return VehicleRideResponseDTO
                .builder()
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .type(vehicle.getType())
                .numberOfSeats(vehicle.getNumberOfSeats())
                .babiesAllowed(vehicle.getBabiesAllowed())
                .petsAllowed(vehicle.getPetsAllowed())
                .rating(vehicle.getRating())
                .build();
    }

    public List<VehicleDTO> toDTOList(List<Vehicle> vehicles) {
        if (vehicles == null) {
            return null;
        }

        return vehicles.stream()
                .map(this::toDTO)
                .toList();
    }
    
    public Vehicle toEntity(VehicleRequestDTO vehicleRequestDTO) {
        if (vehicleRequestDTO == null) {
            return null;
        }

        return Vehicle.builder()
                .brand(vehicleRequestDTO.getBrand())
                .model(vehicleRequestDTO.getModel())
                .type(vehicleRequestDTO.getType())
                .licenceNumber(vehicleRequestDTO.getLicenceNumber())
                .numberOfSeats(vehicleRequestDTO.getNumberOfSeats())
                .babiesAllowed(vehicleRequestDTO.getBabiesAllowed())
                .petsAllowed(vehicleRequestDTO.getPetsAllowed())
                .build();
    }
}
