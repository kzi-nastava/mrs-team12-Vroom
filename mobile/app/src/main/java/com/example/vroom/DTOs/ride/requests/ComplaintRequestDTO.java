package com.example.vroom.DTOs.ride.requests;

public class ComplaintRequestDTO {
    String complaintBody;

    public ComplaintRequestDTO() {
    }

    public ComplaintRequestDTO(String complaintBody) {
        this.complaintBody = complaintBody;
    }

    public String getComplaintBody() {
        return complaintBody;
    }

    public void setComplaintBody(String complaintBody) {
        this.complaintBody = complaintBody;
    }
}
