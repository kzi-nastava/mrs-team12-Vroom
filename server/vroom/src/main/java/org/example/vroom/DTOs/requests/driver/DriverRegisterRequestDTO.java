package org.example.vroom.DTOs.requests.driver;

import lombok.*;
import org.example.vroom.DTOs.requests.vehicle.VehicleRequestDTO;
import org.example.vroom.enums.Gender;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverRegisterRequestDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private Gender gender;
    private MultipartFile profilePhoto;
    private String password;

    private VehicleRequestDTO vehicle;
}