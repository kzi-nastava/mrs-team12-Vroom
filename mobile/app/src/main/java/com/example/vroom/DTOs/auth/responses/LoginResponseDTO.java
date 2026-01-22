package com.example.vroom.DTOs.auth.responses;

public class LoginResponseDTO {
    private Long userID;
    private String type;
    private String token;
    private Long expires;

    public LoginResponseDTO(Long userID, String type, String token, Long expires) {
        this.userID = userID;
        this.type = type;
        this.token = token;
        this.expires = expires;
    }

    public LoginResponseDTO() {
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
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

    public Long getExpires() {
        return expires;
    }

    public void setExpires(Long expires) {
        this.expires = expires;
    }
}
