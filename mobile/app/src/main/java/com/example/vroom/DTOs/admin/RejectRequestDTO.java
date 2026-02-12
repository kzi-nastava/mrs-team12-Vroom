package com.example.vroom.DTOs.admin;

import com.google.gson.annotations.SerializedName;

public class RejectRequestDTO {

    @SerializedName("comment")
    private String comment;

    public RejectRequestDTO() {}

    public RejectRequestDTO(String comment) {
        this.comment = comment;
    }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
