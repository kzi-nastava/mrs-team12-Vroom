package org.example.vroom.DTOs.requests.driver;

import lombok.*;
import org.example.vroom.DTOs.requests.vehicle.VehicleRequestDTO;
import org.example.vroom.enums.Gender;

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
    private byte[] profilePhoto;
    private String password;

    private VehicleRequestDTO vehicle;
}