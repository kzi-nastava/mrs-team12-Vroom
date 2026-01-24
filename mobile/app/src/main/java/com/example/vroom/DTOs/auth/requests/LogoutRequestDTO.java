package com.example.vroom.DTOs.auth.requests;

public class LogoutRequestDTO {
    private String type;

    public LogoutRequestDTO(String type) {
        this.type = type;
    }
    public LogoutRequestDTO() {
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
