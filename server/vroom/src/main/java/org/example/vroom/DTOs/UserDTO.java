package org.example.vroom.DTOs;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.vroom.enums.Gender;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private String phoneNumber;
    private String address;
    private byte[] profilePhoto;
    private String blockedReason;
}
