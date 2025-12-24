package org.example.vroom.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.vroom.enums.DriverStatus;

@Entity
@DiscriminatorValue("DRIVER")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Driver extends User {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverStatus status;


    private Long ratingCount = 0L;
    private Long ratingSum = 0L;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="vehicle_id", nullable = false)
    private Vehicle vehicle;

}
