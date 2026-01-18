package org.example.vroom.entities;


import jakarta.persistence.*;
import lombok.*;
import org.example.vroom.enums.RideStatus;

import java.time.*;
import java.util.*;


@Entity
@Table(name = "rides")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private RegisteredUser passenger;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "routes_id", nullable = false)
    private Route route;


    @Column(nullable = true)
    private LocalDateTime startTime;

    @Column(nullable = true)
    private LocalDateTime endTime;

    @ElementCollection
    @Column(nullable = true)
    private ArrayList<String> passengers;

    @Column(nullable = true)
    private double price;

    @Column(nullable = false)
    private RideStatus status;

    @Column(nullable = true)
    private String cancelReason;

    @Column(nullable = false)
    private Boolean isScheduled;

    @ElementCollection
    @Column(nullable = true)
    private ArrayList<String> complaints;

    @Column(nullable = false)
    private Boolean panicActivated;

    @Column(nullable = true)
    private Integer driverRating;

    @Column(nullable = true)
    private Integer vehicleRating;

    @Column(nullable = true)
    private String comment;

}
