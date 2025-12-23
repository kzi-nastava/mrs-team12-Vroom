package org.example.vroom.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.vroom.enums.VehicleType;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "pricelists")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pricelist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @Column(nullable = false)
    private Map<VehicleType, Float> typePrice = new HashMap<>();
    
    @Column(nullable = false)
    private float pricePerKm;

    protected Pricelist(
            Map<VehicleType, Float> typePrice,
            float pricePerKm
    ) {
        this.typePrice = typePrice;
        this.pricePerKm = pricePerKm;
    }
}

