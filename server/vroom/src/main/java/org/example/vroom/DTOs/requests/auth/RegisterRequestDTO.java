package org.example.vroom.DTOs.requests.auth;

import lombok.*;
import org.example.vroom.enums.Gender;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private Gender gender;
    private byte[] profilePhoto;
    private String password;
    private String type;

    public RegisterRequestDTO(
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            String address,
            String gender,
            String password
    ){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.gender = Gender.valueOf(gender);
        this.password = password;
    }

    public RegisterRequestDTO(
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            String address,
            String gender,
            String password,
            String type
    ){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.gender = Gender.valueOf(gender);
        this.password = password;
        this.type = type;
    }
}
