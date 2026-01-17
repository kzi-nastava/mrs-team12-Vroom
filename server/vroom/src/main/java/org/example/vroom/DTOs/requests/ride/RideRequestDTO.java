package org.example.vroom.DTOs.requests.ride;

import lombok.*;
import org.example.vroom.DTOs.responses.route.GetRouteResponseDTO;
import org.example.vroom.enums.VehicleType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideRequestDTO {
    private List<String> locations; 
    private List<String> passengersEmails;
    private VehicleType vehicleType;
    private Boolean babiesAllowed;
    private Boolean petsAllowed;
    private Boolean scheduled;
    private LocalDateTime scheduledTime;
    private GetRouteResponseDTO route;
}
