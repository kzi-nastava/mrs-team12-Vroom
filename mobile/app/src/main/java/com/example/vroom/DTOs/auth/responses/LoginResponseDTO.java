package com.example.vroom.DTOs.auth.responses;

public class LoginResponseDTO {
    private String type;
    private String token;
    private Long expires;

    public LoginResponseDTO(String type, String token, Long expires) {
        this.type = type;
        this.token = token;
        this.expires = expires;
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

    public Long getExpires() {
        return expires;
    }

    public void setExpires(Long expires) {
        this.expires = expires;
    }
}
