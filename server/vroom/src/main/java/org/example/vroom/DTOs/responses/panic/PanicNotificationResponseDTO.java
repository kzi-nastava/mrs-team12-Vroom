package org.example.vroom.DTOs.responses.panic;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PanicNotificationResponseDTO {
    private Long id;
    private Long rideID;
    private Long activatedById;
    private LocalDateTime activatedAt;
}
