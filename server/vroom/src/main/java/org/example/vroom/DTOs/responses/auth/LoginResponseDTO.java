package org.example.vroom.DTOs.responses.auth;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private Long userID;
    private String type;
    private String token;
    private Long expires;
}
