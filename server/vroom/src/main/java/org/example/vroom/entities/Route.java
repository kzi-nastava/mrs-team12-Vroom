package org.example.vroom.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "routes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double startLocationLat;

    @Column(nullable = false)
    private Double startLocationLng;

    @Column(nullable = false)
    private Double endLocationLat;

    @Column(nullable = false)
    private Double endLocationLng;

    @ElementCollection
    @CollectionTable(name = "route_path", joinColumns = @JoinColumn(name = "route_id"))
    @OrderColumn(name = "point_index")
    List<Point> stops;
}
