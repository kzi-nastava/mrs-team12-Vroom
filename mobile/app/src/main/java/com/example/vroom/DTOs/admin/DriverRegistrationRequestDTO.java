package com.example.vroom.DTOs.admin;

import com.example.vroom.enums.Gender;
import com.example.vroom.enums.VehicleType;
import com.google.gson.annotations.SerializedName;

public class DriverRegistrationRequestDTO {

    @SerializedName("email")
    private String email;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("address")
    private String address;

    @SerializedName("gender")
    private Gender gender;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("profilePhoto")
    private byte[] profilePhoto;

    @SerializedName("brand")
    private String brand;

    @SerializedName("model")
    private String model;

    @SerializedName("type")
    private VehicleType type;

    @SerializedName("licenceNumber")
    private String licenceNumber;

    @SerializedName("numberOfSeats")
    private Integer numberOfSeats;

    @SerializedName("babiesAllowed")
    private Boolean babiesAllowed;

    @SerializedName("petsAllowed")
    private Boolean petsAllowed;

    // Constructor
    public DriverRegistrationRequestDTO() {}

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public byte[] getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(byte[] profilePhoto) { this.profilePhoto = profilePhoto; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public VehicleType getType() { return type; }
    public void setType(VehicleType type) { this.type = type; }

    public String getLicenceNumber() { return licenceNumber; }
    public void setLicenceNumber(String licenceNumber) { this.licenceNumber = licenceNumber; }

    public Integer getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(Integer numberOfSeats) { this.numberOfSeats = numberOfSeats; }

    public Boolean getBabiesAllowed() { return babiesAllowed; }
    public void setBabiesAllowed(Boolean babiesAllowed) { this.babiesAllowed = babiesAllowed; }

    public Boolean getPetsAllowed() { return petsAllowed; }
    public void setPetsAllowed(Boolean petsAllowed) { this.petsAllowed = petsAllowed; }
}