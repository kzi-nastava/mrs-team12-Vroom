package com.example.vroom.DTOs.auth.responses;

public class LoginResponseDTO {
    private String type;
    private String token;
    private Long userId;

    public LoginResponseDTO(String type, String token, Long userID) {
        this.type = type;
        this.token = token;
        this.userId = userID;
    }

    public LoginResponseDTO() {
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userID) {
        this.userId = userID;
    }
}
