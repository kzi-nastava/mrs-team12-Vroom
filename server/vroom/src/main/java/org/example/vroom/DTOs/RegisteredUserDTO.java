package org.example.vroom.DTOs;

import org.example.vroom.enums.UserStatus;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisteredUserDTO extends UserDTO {
    private UserStatus status;

    public RegisteredUserDTO(String firstName, String lastName, String email,
                             String phoneNumber, String address, byte[] profilePhoto,
                             String blockedReason, UserStatus status) {
        super(firstName, lastName, email, phoneNumber, address, profilePhoto, blockedReason);
        this.status = status;
    }
}
