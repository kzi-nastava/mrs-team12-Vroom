package org.example.vroom.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.vroom.enums.DriverStatus;

@Entity
@DiscriminatorValue("DRIVER")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Driver extends User {
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private DriverStatus status;

    @Column
    @Builder.Default
    private Long ratingCount = 0L;

    @Column
    @Builder.Default
    private Long ratingSum = 0L;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="vehicle_id", nullable = true)
    private Vehicle vehicle;

        @Override
    public String getRoleName() {
        return "DRIVER";
    }
    /*@Override
    public boolean isEnabled(){
        return true;
    }*/

    public double getRating(){
        return ratingCount !=0 ?  (double) ratingSum / ratingCount : 0;
    }
}


