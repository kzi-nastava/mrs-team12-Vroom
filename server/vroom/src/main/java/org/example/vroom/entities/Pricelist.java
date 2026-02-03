package org.example.vroom.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.example.vroom.enums.VehicleType;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "pricelists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pricelist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean valid;

    @Column(nullable = false)
    private double price_standard;

    @Column(nullable = false)
    private double price_luxury;

    @Column(nullable = false)
    private double price_minivan;

}

