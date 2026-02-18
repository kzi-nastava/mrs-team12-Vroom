package com.example.vroom.DTOs.ride.requests;

public class LeaveReviewRequestDTO {
    Integer driverRating;
    Integer vehicleRating;
    String comment;

    public Integer getVehicleRating() {
        return vehicleRating;
    }

    public void setVehicleRating(Integer vehicleRating) {
        this.vehicleRating = vehicleRating;
    }

    public Integer getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(Integer driverRating) {
        this.driverRating = driverRating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LeaveReviewRequestDTO() {
    }

    public LeaveReviewRequestDTO(Integer driverRating, Integer vehicleRating, String comment) {
        this.driverRating = driverRating;
        this.vehicleRating = vehicleRating;
        this.comment = comment;
    }
}
