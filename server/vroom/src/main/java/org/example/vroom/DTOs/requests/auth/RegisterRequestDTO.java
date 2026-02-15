package org.example.vroom.DTOs.requests.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.vroom.enums.Gender;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String address;

    @NotNull
    private Gender gender;
    //private MultipartFile profilePhoto;
    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword;


    public RegisterRequestDTO(
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            String address,
            String gender,
            String password,
            String confirmPassword
    ){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.gender = Gender.valueOf(gender);
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
}
