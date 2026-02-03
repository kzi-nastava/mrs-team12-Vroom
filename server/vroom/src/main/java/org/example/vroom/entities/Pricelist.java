package org.example.vroom.entities;

import jakarta.persistence.*;
import lombok.*;

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
    private double priceStandard;

    @Column(nullable = false)
    private double priceLuxury;

    @Column(nullable = false)
    private double priceMinivan;

}

