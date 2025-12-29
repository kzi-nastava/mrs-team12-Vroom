package org.example.vroom.DTOs.requests;


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
}
