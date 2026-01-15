package org.example.vroom.DTOs.responses.geocode;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressSuggestionResponseDTO {
    private String label;
    private double lat;
    private double lon;
}
