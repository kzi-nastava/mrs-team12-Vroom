package com.example.vroom.DTOs.auth.requests;

public class LogoutRequestDTO {
    private Long id;
    private String type;

    public LogoutRequestDTO(Long id, String type) {
        this.id = id;
        this.type = type;
    }
    public LogoutRequestDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
