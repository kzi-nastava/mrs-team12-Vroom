package com.example.vroom.DTOs.ride.responses;

public class CreateFavoriteRouteRequestDTO {
    private Long rideId;
    private String name;

    public CreateFavoriteRouteRequestDTO(Long rideId, String name) {
        this.rideId=rideId;
        this.name=name;
    }

    public Long getRideId() {
        return rideId;
    }
    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
