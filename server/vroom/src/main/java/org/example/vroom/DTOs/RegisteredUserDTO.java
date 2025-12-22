package org.example.vroom.DTOs;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisteredUserDTO extends UserDTO {
    private String status; // change with user status enum

    public RegisteredUserDTO(String firstName, String lastName, String email,
                             String phoneNumber, String address, byte[] profilePhoto,
                             String blockedReason, String status) {
        super(firstName, lastName, email, phoneNumber, address, profilePhoto, blockedReason);
        this.status = status;
    }
}
