package org.example.vroom.DTOs;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class RegisteredUserDTO extends UserDTO {
    private String status; // change with user status enum

}
