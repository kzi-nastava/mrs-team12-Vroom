package com.example.vroom.DTOs.auth.requests;

public class ForgotPasswordRequestDTO {
    private String email;

    public ForgotPasswordRequestDTO(String email) {
        this.email = email;
    }

    public ForgotPasswordRequestDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
