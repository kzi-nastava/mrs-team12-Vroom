package com.example.vroom.DTOs.admin;

import com.google.gson.annotations.SerializedName;

public class AdminUserDTO {
    private Long id;
    private String email;

    @SerializedName("FIRST_NAME")
    private String firstName;

    @SerializedName("LAST_NAME")
    private String lastName;

    @SerializedName("PHONE_NUMBER")
    private String telephoneNumber;

    @SerializedName("ADDRESS")
    private String address;

    @SerializedName("BLOCKED_REASON")
    private Boolean blocked;

    @SerializedName("TYPE")
    private String type;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getTelephoneNumber() { return telephoneNumber; }
    public void setTelephoneNumber(String telephoneNumber) { this.telephoneNumber = telephoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Boolean getBlocked() { return blocked; }
    public void setBlocked(Boolean blocked) { this.blocked = blocked; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}