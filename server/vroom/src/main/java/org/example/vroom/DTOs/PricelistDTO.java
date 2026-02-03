package org.example.vroom.DTOs;


import jakarta.persistence.Column;
import lombok.*;
import org.example.vroom.enums.VehicleType;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricelistDTO {

    private double price_standard;
    private double price_luxury;
    private double price_minivan;
}
