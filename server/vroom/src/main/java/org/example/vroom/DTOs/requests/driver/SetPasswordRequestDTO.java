package org.example.vroom.DTOs.requests.driver;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetPasswordRequestDTO {
    private String password;
    private String confirmPassword;
}