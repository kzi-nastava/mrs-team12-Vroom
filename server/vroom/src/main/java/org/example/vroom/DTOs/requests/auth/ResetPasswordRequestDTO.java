package org.example.vroom.DTOs.requests.auth;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequestDTO {
    private String email;
    private String code;
    private String password;
    private String confirmPassword;
}
