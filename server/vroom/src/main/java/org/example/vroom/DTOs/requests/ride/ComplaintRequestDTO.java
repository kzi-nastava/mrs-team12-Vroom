package org.example.vroom.DTOs.requests.ride;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintRequestDTO {

    @NotBlank(message = "Complaint should not be empty")
    String complaintBody;
}
