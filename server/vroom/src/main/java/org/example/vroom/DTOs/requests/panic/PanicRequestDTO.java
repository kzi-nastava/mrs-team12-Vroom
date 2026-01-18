package org.example.vroom.DTOs.requests.panic;

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
    private Long userId;
    private LocalDateTime activatedAt;
}
