package org.example.vroom.DTOs.responses;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private Long userID;
    private String token;
    private String type;
    private Long expiresIn;
    private Long expiresAt;
}
