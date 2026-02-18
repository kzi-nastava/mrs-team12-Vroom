package org.example.vroom.DTOs.requests.panic;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PanicRequestDTO {
    @NotNull
    @Positive
    private Long rideId;

    @PastOrPresent
    private LocalDateTime activatedAt;
}
