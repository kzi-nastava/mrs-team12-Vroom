package com.example.vroom.DTOs.driver.requests;


import com.example.vroom.enums.Gender;

public class DriverRideResponseDTO {
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private double rating;
    private VehicleRideResponseDTO vehicle;

    public DriverRideResponseDTO() {
        this.rating = 0.0;
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public VehicleRideResponseDTO getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleRideResponseDTO vehicle) {
        this.vehicle = vehicle;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}