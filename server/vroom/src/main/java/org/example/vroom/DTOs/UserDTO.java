package org.example.vroom.DTOs;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class UserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String phoneNumber;
    private String address;
    private byte[] profilePhoto;
    private String blockedReason;

}
