package org.example.vroom.entities;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Point {
    private Double lat;
    private Double lng;
}
