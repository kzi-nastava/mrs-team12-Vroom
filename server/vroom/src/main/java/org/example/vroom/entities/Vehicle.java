package org.example.vroom.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.vroom.enums.VehicleType;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type;

    @Column(nullable = false, unique = true)
    private String licenceNumber;

    @Column
    private Integer numberOfSeats;

    @Column
    private Boolean babiesAllowed;

    @Column
    private Boolean petsAllowed;

    @Column
    @Builder.Default
    private Long ratingCount = 0L;

    @Column
    @Builder.Default
    private Long ratingSum = 0L;
}
