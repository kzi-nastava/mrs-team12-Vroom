package org.example.vroom.DTOs.requests;

import org.example.vroom.enums.Gender;
import org.example.vroom.enums.VehicleType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DriverRegistrationRequestDTO {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String address;
    private Gender gender;
    private String phoneNumber;
    private byte[] profilePhoto;

    private String model;
    private VehicleType type;
    private String licenceNumber;
    private Integer numberOfSeats;
    private Boolean babiesAllowed;
    private Boolean petsAllowed;
}
