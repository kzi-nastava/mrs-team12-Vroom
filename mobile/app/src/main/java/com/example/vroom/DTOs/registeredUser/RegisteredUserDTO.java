package com.example.vroom.DTOs.registeredUser;

public class RegisteredUserDTO {
    public Long id;
    public String firstName;
    public String lastName;
    public String email;
    public String gender;
    public String phoneNumber;
    public String address;
    public String blockedReason;
    public String status;
    public String profilePhoto;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }
}
