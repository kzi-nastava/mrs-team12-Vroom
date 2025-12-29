package org.example.vroom.DTOs;

import org.example.vroom.enums.UserStatus;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class RegisteredUserDTO extends UserDTO {
    private UserStatus status;

}
