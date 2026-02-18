package com.example.vroom.DTOs.driver.requests;

import com.example.vroom.enums.DriverStatus;
import com.example.vroom.enums.Gender;

public class DriverDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String phoneNumber;

    private Gender gender;
    private String status;
    private Long ratingCount;
    private Long ratingSum;

    private VehicleDTO vehicle;


    public Gender getGender(){
        return gender;
    }
    public void setGender(Gender gender){
        this.gender=gender;
    }
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Long ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Long getRatingSum() {
        return ratingSum;
    }

    public void setRatingSum(Long ratingSum) {
        this.ratingSum = ratingSum;
    }

    public VehicleDTO getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleDTO vehicle) {
        this.vehicle = vehicle;
    }


    public static class VehicleDTO {
        private Long id;
        private String brand;
        private String model;
        private String licenceNumber;
        private Integer numberOfSeats;
        private Boolean babiesAllowed;
        private Boolean petsAllowed;

     
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

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

        public String getLicenceNumber() {
            return licenceNumber;
        }

        public void setLicenceNumber(String licenceNumber) {
            this.licenceNumber = licenceNumber;
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
    }
}
