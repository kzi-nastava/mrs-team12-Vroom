package com.example.vroom.services;

import com.example.vroom.DTOs.ride.responses.RideResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RegisteredUserService {
    @GET("api/registered-user/ride")
    Call<List<RideResponseDTO>> getRides();
}
