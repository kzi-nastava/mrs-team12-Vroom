package org.example.vroom.DTOs;


import lombok.*;
import org.example.vroom.enums.VehicleType;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricelistDTO {

    private Map<VehicleType, Float> typePrice;

    private float pricePerKm;
}
