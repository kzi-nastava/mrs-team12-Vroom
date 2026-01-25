package com.example.vroom.DTOs.auth.requests;

public class ResetPasswordRequestDTO {
    private String email;
    private String code;
    private String password;

    public ResetPasswordRequestDTO(String email, String code, String password) {
        this.email = email;
        this.code = code;
        this.password = password;
    }

    public ResetPasswordRequestDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
