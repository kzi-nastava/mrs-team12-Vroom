package org.example.vroom.DTOs.responses.geocode;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeoapifyAddressFeatureDTO {
    private GeoapifyAddressPropertiesDTO properties;
}
