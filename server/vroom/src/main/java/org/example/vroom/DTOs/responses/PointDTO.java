package org.example.vroom.DTOs.responses;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointDTO {
    private Double lat;
    private Double lng;
}
