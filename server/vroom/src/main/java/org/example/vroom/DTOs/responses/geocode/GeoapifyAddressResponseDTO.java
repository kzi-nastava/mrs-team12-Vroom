package org.example.vroom.DTOs.responses.geocode;


import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class GeoapifyAddressResponseDTO {
    private List<GeoapifyAddressFeatureDTO> features;
}
