package org.example.vroom.DTOs.requests.auth;

import lombok.*;
import org.example.vroom.enums.Gender;
import org.springframework.web.multipart.MultipartFile;

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
    private MultipartFile profilePhoto;
    private String password;

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
}
