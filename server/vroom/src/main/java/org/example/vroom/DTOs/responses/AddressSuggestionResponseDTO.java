package org.example.vroom.DTOs.responses;

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
