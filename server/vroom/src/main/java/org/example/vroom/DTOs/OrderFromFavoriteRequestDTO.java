package org.example.vroom.DTOs;

import java.time.LocalDateTime;

import org.example.vroom.enums.VehicleType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderFromFavoriteRequestDTO {

    private Long favoriteRouteId;
    private VehicleType vehicleType;
    private Boolean babiesAllowed;
    private Boolean petsAllowed;
    private LocalDateTime scheduledTime; 
}
