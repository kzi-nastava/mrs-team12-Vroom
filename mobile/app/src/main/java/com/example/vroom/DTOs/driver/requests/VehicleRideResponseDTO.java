package com.example.vroom.DTOs.driver.requests;

import com.example.vroom.enums.VehicleType;

public class VehicleRideResponseDTO {
    private String brand;
    private String model;
    private String licensePlate;
    private VehicleType type;
    private Integer numberOfSeats;
    private Boolean babiesAllowed;
    private Boolean petsAllowed;
    private double rating;

    public VehicleRideResponseDTO() {
        this.rating = 0.0;
    }

    // Getters and Setters
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public Boolean getBabiesAllowed() {
        return babiesAllowed;
    }

    public void setBabiesAllowed(Boolean babiesAllowed) {
        this.babiesAllowed = babiesAllowed;
    }

    public Boolean getPetsAllowed() {
        return petsAllowed;
    }

    public void setPetsAllowed(Boolean petsAllowed) {
        this.petsAllowed = petsAllowed;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getFullVehicleName() {
        return brand + " " + model;
    }
}